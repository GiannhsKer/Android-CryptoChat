package com.gi.cryptochat.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gi.cryptochat.Constants.AUTHENTICATION_VM
import com.gi.cryptochat.Constants.EMAIL_EMPTY
import com.gi.cryptochat.Constants.EMAIL_INVALID
import com.gi.cryptochat.Constants.LOGIN
import com.gi.cryptochat.Constants.PASSWORD_EMPTY
import com.gi.cryptochat.Constants.PASSWORD_MISMATCH
import com.gi.cryptochat.Constants.PASSWORD_WEAK
import com.gi.cryptochat.Constants.REGISTER
import com.gi.cryptochat.Constants.USERNAME_EMPTY
import com.gi.cryptochat.Constants.USERNAME_INVALID
import com.gi.cryptochat.Constants.USERNAME_TAKEN
import com.gi.cryptochat.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = ""
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = FirebaseFirestore.getInstance()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _snackbarMessage = MutableSharedFlow<String>(replay = 1)
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun snackbarSuccess(message: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun authUser(username: String, email: String, password: String, action: String) {

        if (_uiState.value.loading) return

        _uiState.update { it.copy(loading = true, error = null) }

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password cannot be empty") }
            return
        }

        when (action) {
            LOGIN -> {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        _uiState.update {
                            it.copy(loading = false, onSuccess = true, error = null)
                        }
                    }
                    .addOnFailureListener { error ->
                        _uiState.update {
                            it.copy(
                                loading = false,
                                onSuccess = false,
                                error = error.localizedMessage ?: "Login failed"
                            )
                        }
                    }
            }

            REGISTER -> {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: return@addOnSuccessListener

                        result.user?.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()
                        )?.addOnFailureListener {
                            Log.e(AUTHENTICATION_VM, "Failed to set display name", it)
                        }

                        val user = User(
                            uid = uid,
                            username = username,
                            email = email,
                            password = password
                        )

                        firestore.collection("users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener {
                                snackbarSuccess("Registration successful")
                                _uiState.update {
                                    it.copy(loading = false, onSuccess = true)
                                }
                            }
                            .addOnFailureListener { error ->
                                Log.e(AUTHENTICATION_VM, "Error saving user: ", error)
                                _uiState.update {
                                    it.copy(
                                        loading = false,
                                        onSuccess = false,
                                        error = error.localizedMessage ?: "Unknown error"
                                    )
                                }
                            }
                    }
                    .addOnFailureListener { error ->
                        Log.e(AUTHENTICATION_VM, "Registration failed: ", error)
                        _uiState.update {
                            it.copy(
                                loading = false,
                                onSuccess = false,
                                error = error.localizedMessage ?: "Unknown error"
                            )
                        }
                    }
            }
        }
    }

    fun validateAndRegisterUser(
        email: String,
        username: String,
        password: String,
        confirmPassword: String,
        onResult: (String?) -> Unit
    ) {
        val error = validateTextFieldsSync(email, username, password, confirmPassword)
        if (error != null) {
            onResult(error)
            return
        }

        viewModelScope.launch {
            val usernameTaken = isUsernameTaken(username)
            if (usernameTaken) {
                onResult(USERNAME_TAKEN)
            } else {
                onResult(null) // all good, proceed with registration
            }
        }
    }

    fun validateTextFieldsSync(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            email.isBlank() -> EMAIL_EMPTY
            !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()) ->
                EMAIL_INVALID

            username.isBlank() -> USERNAME_EMPTY
            !username.matches("[a-zA-Z0-9]{1,10}".toRegex()) -> USERNAME_INVALID
            password.isBlank() -> PASSWORD_EMPTY
            password.length < 8
                    || !password.matches(
                "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$"
                    .toRegex()
            ) -> PASSWORD_WEAK

            confirmPassword != password -> PASSWORD_MISMATCH
            else -> null
        }
    }

    suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val snapshot = Firebase.firestore
                .collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            !snapshot.isEmpty
        } catch (error: Exception) {
            Log.e(AUTHENTICATION_VM, "Error trying to validate username, $error")
            true // treating as taken
        }
    }
}

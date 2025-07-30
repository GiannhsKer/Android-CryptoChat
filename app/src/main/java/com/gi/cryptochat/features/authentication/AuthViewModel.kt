package com.gi.cryptochat.features.authentication

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.gi.cryptochat.Constants.EMAIL_EMPTY
import com.gi.cryptochat.Constants.EMAIL_INVALID
import com.gi.cryptochat.Constants.LOG_IN
import com.gi.cryptochat.Constants.PASSWORD_EMPTY
import com.gi.cryptochat.Constants.PASSWORD_MISMATCH
import com.gi.cryptochat.Constants.PASSWORD_WEAK
import com.gi.cryptochat.Constants.REGISTER
import com.gi.cryptochat.Constants.USERNAME_EMPTY
import com.gi.cryptochat.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun authUser(username: String?, email: String, password: String, action: String) {

        if (_uiState.value.loading) return

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password cannot be empty") }
            return
        }

        _uiState.update { it.copy(loading = true, error = null) }

        when (action) {
            LOG_IN -> {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        _uiState.update {
                            it.copy(loading = false, onSuccess = true, error = null)
                        }
                    }
                    .addOnFailureListener { e ->
                        _uiState.update {
                            it.copy(
                                loading = false,
                                onSuccess = false,
                                error = e.localizedMessage ?: "Login failed"
                            )
                        }
                    }
            }

            REGISTER -> {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: return@addOnSuccessListener

                        val user = User(
                            uid = uid,
                            username = username.toString(),
                            email = email,
                            password = password
                        )

                        firestore.collection("users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener {
                                Log.d("RegisterViewModel", "User object saved successfully.")
                                _uiState.update {
                                    it.copy(loading = false, onSuccess = true)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("RegisterViewModel", "Error saving user object: ", e)
                                _uiState.update {
                                    it.copy(
                                        loading = false,
                                        onSuccess = false,
                                        error = e.localizedMessage ?: "Unknown error"
                                    )
                                }
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("RegisterViewModel", "Registration failed: ", e)
                        _uiState.update {
                            it.copy(
                                loading = false,
                                onSuccess = false,
                                error = e.localizedMessage ?: "Unknown error"
                            )
                        }
                    }
            }
        }
    }

    fun validateTextFields(
        email: String,
        username: String,
        password: String,
        confirmPassword: MutableState<TextFieldValue>
    ): String {
        return when {
            email.isBlank() -> EMAIL_EMPTY
            !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()) ->
                EMAIL_INVALID

            username.isBlank() -> USERNAME_EMPTY
            password.isBlank() -> PASSWORD_EMPTY
            password.length < 8
                    || !password.matches(
                "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$"
                    .toRegex()
            ) -> PASSWORD_WEAK

            confirmPassword.value.text != password -> PASSWORD_MISMATCH
            else -> ""
        }
    }
}

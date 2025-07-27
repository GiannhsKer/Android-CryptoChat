package com.gi.cryptochat.view.register

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.gi.cryptochat.Constants.EMAIL_EMPTY
import com.gi.cryptochat.Constants.EMAIL_INVALID
import com.gi.cryptochat.Constants.PASSWORD_EMPTY
import com.gi.cryptochat.Constants.PASSWORD_MISMATCH
import com.gi.cryptochat.Constants.PASSWORD_WEAK
import com.gi.cryptochat.Constants.USERNAME_EMPTY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun registerUser(home: () -> Unit, username: String) {
        if (!_loading.value) {
            val email: String = _email.value ?: throw IllegalArgumentException("email expected")
            val password: String =
                _password.value ?: throw IllegalArgumentException("password expected")

            _loading.value = true

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            val user = hashMapOf(
                                "uid" to uid,
                                "email" to email,
                                "username" to username
                            )
                            Firebase.firestore.collection("users").document(uid).set(user)
                        }
                        home()
                    }
                    _loading.value = false
                }
        }
    }

    fun validateTextFields(
        email: String,
        username: MutableState<String>,
        password: String,
        confirmPassword: MutableState<TextFieldValue>
    ): String {
        return when {
            email.isBlank() -> EMAIL_EMPTY
            !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()) ->
                EMAIL_INVALID

            username.value.isBlank() -> USERNAME_EMPTY
            password.isBlank() -> PASSWORD_EMPTY
            password.length < 8 ||
                    !password
                        .matches(
                            "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$"
                                .toRegex()
                        ) ->
                PASSWORD_WEAK

            confirmPassword.value.text != password -> PASSWORD_MISMATCH
            else -> ""
        }
    }
}
package com.gi.cryptochat.features.register

import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun registerUser(username: String, email: String, password: String) {

        if (!_loading.value) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

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
                            Log.d("RegisterViewModel", "User object saved successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("RegisterViewModel", "Error saving user object: ", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("RegisterViewModel", "Registration failed: ", e)
                }
            _loading.value = false
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
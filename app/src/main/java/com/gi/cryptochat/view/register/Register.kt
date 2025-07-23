package com.gi.cryptochat.view.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gi.cryptochat.R


@Composable
fun RegisterView(
    home: () -> Unit,
    back: () -> Unit,
    registerViewModel: RegisterViewModel = viewModel()
) {
    val email: String by registerViewModel.email.observeAsState("")
    val password: String by registerViewModel.password.observeAsState("")
    val loading: Boolean by registerViewModel.loading.observeAsState(initial = false)

    val username = remember { mutableStateOf("") }
    val confirm = remember { mutableStateOf(TextFieldValue()) }
    val showPass = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val dialogText = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    fun validateTextFields(): Boolean {
        if (email.isBlank()) {
            dialogText.value = "Please enter your email."
            showDialog.value = true
            return false
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex())) {
            dialogText.value = "Email is invalid."
            showDialog.value = true
            return false
        }

        if (username.value.isBlank()) {
            dialogText.value = "Please enter your username."
            showDialog.value = true
            return false
        }

        if (password.isBlank()) {
            dialogText.value = "Please enter a password."
            showDialog.value = true
            return false
        }

        if (password.length < 8 || !password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+\$".toRegex())) {
            dialogText.value = "Password should be at least 8 characters long and include letters, numbers, and symbols."
            showDialog.value = true
            return false
        }

        if (confirm.value.text != password) {
            dialogText.value = "Password and confirm password do not match."
            showDialog.value = true
            return false
        }

        return true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(scrollState)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Appbar(title = "Register", action = back)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.steps),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextFormField(
                        value = email,
                        onValueChange = { registerViewModel.updateEmail(it) },
                        label = "Email",
                        keyboardType = KeyboardType.Email,
                        visualTransformation = VisualTransformation.None
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextFormField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = "Username",
                        keyboardType = KeyboardType.Text,
                        visualTransformation = VisualTransformation.None
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextFormField(
                        value = password,
                        onValueChange = { registerViewModel.updatePassword(it) },
                        label = "Password",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (!showPass.value) PasswordVisualTransformation() else VisualTransformation.None
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextFormField(
                        value = confirm.value.text,
                        onValueChange = { confirm.value = TextFieldValue(it) },
                        label = "Confirm Password",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (!showPass.value) PasswordVisualTransformation() else VisualTransformation.None
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = showPass.value,
                            onCheckedChange = { showPass.value = !showPass.value },
                            colors = CheckboxDefaults.colors(checkedColor = Color(26, 115, 232))
                        )
                        Text(
                            text = "Show password",
                            color = Color.Black,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Buttons(
                        title = "Register",
                        onClick = {
                            if (validateTextFields()) {
                                registerViewModel.registerUser(
                                    home = home,
                                    username = username.value
                                )
                            }
                        },
//                        backgroundColor = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Alert", color = Color(32, 33, 36)) },
                text = { Text(dialogText.value, color = Color(95, 99, 104)) },
                confirmButton = {

                    Buttons(
                        title = "Ok",
                        onClick = { showDialog.value = false },
//                        backgroundColor = Color.Black
                        modifier = Modifier.width(100.dp) // 👈 Set your desired width here

                    )
                },

                shape = RoundedCornerShape(15.dp),
                containerColor = Color.White
            )
        }
    }
}

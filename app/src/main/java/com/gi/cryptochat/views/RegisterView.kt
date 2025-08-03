package com.gi.cryptochat.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gi.cryptochat.AppAlertDialog
import com.gi.cryptochat.Constants.REGISTER
import com.gi.cryptochat.R
import com.gi.cryptochat.SetStatusBarAppearance
import com.gi.cryptochat.viewmodels.AuthViewModel
import com.gi.cryptochat.getStatusBarHeight
import com.gi.cryptochat.gradientBrush

@Composable
fun RegisterView(
    auth: () -> Unit = {},
    back: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val loading: Boolean by authViewModel.loading.collectAsState(false)

    var showDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isChecked by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val uiState by authViewModel.uiState.collectAsState()

    SetStatusBarAppearance(
        useDarkIcons = false
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .height(56.dp + getStatusBarHeight())
                .background(brush = gradientBrush),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = getStatusBarHeight() + 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = back,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Register",
                    Modifier.weight(1f),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(scrollState)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.steps),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (!isChecked) PasswordVisualTransformation() else VisualTransformation.None
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (!isChecked) PasswordVisualTransformation() else VisualTransformation.None
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(26, 115, 232))
                        )
                        Text(
                            text = "Show password",
                            fontSize = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(gradientBrush)
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clickable(onClick = {
                                authViewModel.validateAndRegisterUser(
                                        email,
                                        username,
                                        password,
                                        confirmPassword
                                    ) { error ->
                                    if (error != null) {
                                        errorMessage.value = error
                                        showDialog.value = !showDialog.value
                                    } else {
                                        authViewModel.authUser(username, email, password, REGISTER)
                                    }
                                }
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Register",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        if (showDialog.value) {
            AppAlertDialog(
                message = errorMessage.value,
                onDismiss = { showDialog.value = false }
            )
        }

        LaunchedEffect(uiState.onSuccess) {
            if (uiState.onSuccess) {
                auth()
            }
        }

        uiState.error?.let { errorMessage ->
            AppAlertDialog(
                message = errorMessage,
                onDismiss = { authViewModel.clearError() }
            )
        }
    }
}
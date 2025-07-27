package com.gi.cryptochat.view.register

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gi.cryptochat.R
import com.gi.cryptochat.SetStatusBarAppearance
import com.gi.cryptochat.getStatusBarHeight
import com.gi.cryptochat.gradientBrush

@Composable
fun RegisterView(
    home: () -> Unit,
    back: () -> Unit = {},
    registerViewModel: RegisterViewModel = viewModel()
) {
    val email: String by registerViewModel.email.collectAsState()
    val password: String by registerViewModel.password.collectAsState("")
    val loading: Boolean by registerViewModel.loading.collectAsState(false)

    val username = remember { mutableStateOf("") }
    val confirm = remember { mutableStateOf(TextFieldValue()) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val dialogText = remember { mutableStateOf("") }
    var isChecked by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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

                    TextFormField(
                        value = email,
                        onValueChange = { registerViewModel.updateEmail(it) },
                        label = "Email",
                        keyboardType = KeyboardType.Email,
                        visualTransformation = VisualTransformation.None
                    )

                    TextFormField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = "Username",
                        keyboardType = KeyboardType.Text,
                        visualTransformation = VisualTransformation.None
                    )

                    TextFormField(
                        value = password,
                        onValueChange = { registerViewModel.updatePassword(it) },
                        label = "Password",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (!isChecked) PasswordVisualTransformation() else VisualTransformation.None
                    )

                    TextFormField(
                        value = confirm.value.text,
                        onValueChange = { confirm.value = TextFieldValue(it) },
                        label = "Confirm Password",
                        keyboardType = KeyboardType.Password,
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
                            color = Color.Black,
                            fontFamily = FontFamily.SansSerif,
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
                                if (registerViewModel.validateTextFields(
                                        email,
                                        username,
                                        password,
                                        confirmPassword = confirm
                                    ).isBlank()
                                ) {
                                    registerViewModel.registerUser(
                                        home = home,
                                        username = username.value
                                    )
                                } else {
                                    showDialog = !showDialog
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

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Alert") },
                text = { Text(dialogText.value) },
                shape = RoundedCornerShape(15.dp),
                containerColor = Color.White,
                confirmButton = {
                    Buttons(
                        title = "Ok",
                        onClick = { showDialog = false },
                        modifier = Modifier.width(100.dp)
                    )
                }
            )
        }
    }
}
package com.example.myapplication.view.authentication

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun AuthenticationView(register: () -> Unit, login: () -> Unit) {
    var selected by remember { mutableStateOf("Login") }

    MyApplicationTheme {
        SetStatusBarAppearance(
            useDarkIcons = true // or false if your background is dark
        )
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            )
            {
                Image(
                    painterResource(id = R.drawable.cryptochat), contentDescription = "app logo",
                    modifier = Modifier.size(250.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    LoggingButton(
                        color = Color.White,
                        buttonText = "Register",
                        selected = selected,
                        onClick = {
                            selected = "Register"
                            register()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    LoggingButton(
                        color = MaterialTheme.colorScheme.primary,
                        buttonText = "Login",
                        selected = selected,
                        onClick = {
                            selected = "Login"
                            login()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SetStatusBarAppearance(useDarkIcons: Boolean) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightStatusBars = useDarkIcons
    }
}

@Composable
fun LoggingButton(
    color: Color,
    buttonText : String,
    selected : String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected == buttonText) color else Color.Transparent,
            contentColor = if (selected == buttonText) Color.White else Color.Black
        )
    ) {
        Text(buttonText)
    }
}
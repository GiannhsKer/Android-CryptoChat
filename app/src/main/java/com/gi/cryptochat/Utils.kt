package com.gi.cryptochat

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.gi.cryptochat.view.register.Buttons

object Constants {

    //    Chat object properties
    const val MESSAGES = "messages"
    const val MESSAGE = "message"
    const val SENT_BY = "sent_by"
    const val SENT_ON = "sent_on"
    const val IS_CURRENT_USER = "is_current_user"

//    Error Messages
    const val EMAIL_EMPTY = "Please enter your email."
    const val EMAIL_INVALID = "Email is invalid."
    const val USERNAME_EMPTY = "Please enter your username."
    const val PASSWORD_EMPTY = "Please enter a password."
    const val PASSWORD_WEAK =
        "Password should be at least 8 characters long and include letters, numbers, and symbols."
    const val PASSWORD_MISMATCH = "Password and confirm password do not match."
}

val gradientBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2196F3), Color(0xFF2575FC))
)

@Composable
fun getStatusBarHeight() : Dp {
    return WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
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
fun alertBox(error : String) : Unit {
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text("Alert", color = Color(32, 33, 36)) },
        text = { Text(dialogText.value, color = Color(95, 99, 104)) },
        confirmButton = {

            Buttons(
                title = "Ok",
                onClick = { showDialog = false },
                modifier = Modifier.width(100.dp) // 👈 Set your desired width here

            )
        },

        shape = RoundedCornerShape(15.dp),
        containerColor = Color.White
    )
}
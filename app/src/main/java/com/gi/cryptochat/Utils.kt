package com.gi.cryptochat

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Constants {

    //    Navigation properties
    const val AUTH_OPTION = "AuthOption"
    const val CHATROOM_LIST = "ChatRoomList"
    const val CHATROOM = "ChatRoom/{roomId}"
    const val LOG_IN = "LogIn"
    const val REGISTER = "Register"

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

    //    Classes for debug
    const val AUTHENTICATION_VM = "Authentication VM"
    const val CHATROOMLIST_VM = "ChatRoomList VM"
}

val gradientBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2196F3), Color(0xFF2575FC))
)

@Composable
fun getStatusBarHeight(): Dp {
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
fun AppAlertDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Ok")
            }
        }
    )
}

@Composable
fun getDateFromLong(timestamp : Long) : String{
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(date)
}

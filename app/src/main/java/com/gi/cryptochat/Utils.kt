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

// Global Constants used throughout the app
object Constants {

    //    Destination properties
    const val AUTH_OPTION = "AuthOption"
    const val CHATROOM_LIST = "ChatRoomList"
    const val CHATROOM = "ChatRoom/{roomName}"
    const val LOGIN = "LogIn"
    const val REGISTER = "Register"

    //    Error Messages
    const val EMAIL_EMPTY = "Please enter your email"
    const val EMAIL_INVALID = "Email is invalid"
    const val USERNAME_EMPTY = "Please enter your username"
    const val USERNAME_INVALID = "Username is invalid"
    const val USERNAME_TAKEN = "Username is taken"
    const val PASSWORD_EMPTY = "Please enter a password"
    const val PASSWORD_WEAK =
        "Password should be at least 8 characters long and include letters, numbers, and symbols."
    const val PASSWORD_MISMATCH = "Password and confirm password do not match."

    //    Classes for debug
    const val NAV_COMPOSE_APP = "NavComposeApp"
    const val AUTHENTICATION_VM = "AuthenticationVM"
    const val CHATROOMLIST_VM = "ChatRoomListVM"
    const val CHAT_VM = "ChatVM"
    const val CHAT_VIEW = "ChatView"
    const val CHATROOMLIST_VIEW = "ChatRoomListView"
    const val AUTHENTICATION_VIEW = "AuthenticationView"

    // etc.
    const val UNKNOWN_DISPLAY_NAME = "UnknownName"
    const val UNKNOWN_ID = "UnknownId"
    const val UNKNOWN_ROOM_NAME = "UnknownRoomName"

}

// Used for coloring screens
val gradientBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2196F3), Color(0xFF2575FC))
)

// Ui state is an object used to update the screen when something is loaded or an error occurs
data class UiState(
    val loading: Boolean = false,
    val onSuccess: Boolean = false,
    val error: String? = null
)

// Gets status' bar height, so the content does not overlap with screen's status bar (top row of the screen)
@Composable
fun getStatusBarHeight(): Dp {
    return WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
}

// Used to make the icons appear white
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

// Used to alert user about errors or prompt to create a new chat room
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

// Used to convert date in Long to Date String
fun getDateFromLong(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yy, HH:mm", Locale.getDefault()).format(Date(timestamp))
}

package com.gi.cryptochat

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Constants {
    const val TAG = "flash-chat"

//    Chat object properties
    const val MESSAGES = "messages"
    const val MESSAGE = "message"
    const val SENT_BY = "sent_by"
    const val SENT_ON = "sent_on"
    const val IS_CURRENT_USER = "is_current_user"

//    Error Messages
    const val EMPTY_EMAIL = "Please enter your email."
    const val INVALID_EMAIL = "Email is invalid."
    const val EMPTY_USERNAME = "Please enter your username."
    const val EMPTY_PASSWORD = "Please enter a password."
    const val WEAK_PASSWORD =
        "Password should be at least 8 characters long and include letters, numbers, and symbols."
    const val PASSWORD_MISMATCH = "Password and confirm password do not match."
}

val gradientBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2196F3), Color(0xFF2575FC))
)
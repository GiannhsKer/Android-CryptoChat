package com.gi.cryptochat.nav

import androidx.navigation.NavHostController

object Destination {
    const val AUTH_OPTION = "authOption"
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val CHATROOM_LIST = "chatRoomList"
    const val CHATROOM = "chatRoom/{roomId}"
}

class Action(navController: NavHostController) {
    val login: () -> Unit = { navController.navigate(Destination.LOGIN) }
    val register: () -> Unit = { navController.navigate(Destination.REGISTER) }
    val navigateBack: () -> Unit = { navController.popBackStack() }
    val chatRoomList: () -> Unit = {
        navController.navigate(Destination.CHATROOM_LIST) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
            restoreState = false
        }
    }
    val chatRoom: (String) -> Unit = { roomId ->
        navController.navigate("chatRoom/$roomId")
    }
    val auth: () -> Unit = { navController.navigate(Destination.AUTH_OPTION) }
}
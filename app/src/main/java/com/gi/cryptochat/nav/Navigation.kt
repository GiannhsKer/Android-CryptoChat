package com.gi.cryptochat.nav

import androidx.navigation.NavHostController

object Destination {
    const val AuthenticationOption = "authenticationOption"
    const val Register = "register"
    const val Login = "login"
    const val Home = "home"
    const val ChatRoomList = "chatRoomList"
    const val ChatRoom = "chatRoom/{roomId}"
}

class Action(navController: NavHostController) {
    val home: () -> Unit = {
        navController.navigate(Destination.Home) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }
    val login: () -> Unit = { navController.navigate(Destination.Login) }
    val register: () -> Unit = { navController.navigate(Destination.Register) }
    val navigateBack: () -> Unit = { navController.popBackStack() }
    val chatRoomList: () -> Unit = {
        navController.navigate(Destination.ChatRoomList) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
            restoreState = false
        }
    }
    val chatRoom: (String) -> Unit = { roomId ->
        navController.navigate("chatRoom/$roomId")
    }
}
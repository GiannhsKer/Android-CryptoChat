package com.gi.cryptochat.navigation

import androidx.navigation.NavHostController
import com.gi.cryptochat.Constants.AUTH_OPTION
import com.gi.cryptochat.Constants.CHATROOM_LIST
import com.gi.cryptochat.Constants.LOGIN
import com.gi.cryptochat.Constants.REGISTER

class Action(navController: NavHostController) {
    val auth: () -> Unit = { navController.navigate(AUTH_OPTION) }
    val login: () -> Unit = { navController.navigate(LOGIN) }
    val register: () -> Unit = { navController.navigate(REGISTER) }
    val navigateBack: () -> Unit = { navController.popBackStack() }
    val chatRoomList: () -> Unit = {
        navController.navigate(CHATROOM_LIST) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
            restoreState = false
        }
    }
    val chatRoom: (String) -> Unit = { roomName ->
        navController.navigate("chatRoom/$roomName")
    }
}
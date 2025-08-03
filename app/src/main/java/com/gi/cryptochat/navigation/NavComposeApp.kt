package com.gi.cryptochat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gi.cryptochat.Constants.AUTH_OPTION
import com.gi.cryptochat.Constants.CHATROOM
import com.gi.cryptochat.Constants.CHATROOM_LIST
import com.gi.cryptochat.Constants.LOGIN
import com.gi.cryptochat.Constants.REGISTER
import com.gi.cryptochat.views.AuthenticationView
import com.gi.cryptochat.views.LoginView
import com.gi.cryptochat.views.RegisterView
import com.gi.cryptochat.views.ChatView
import com.gi.cryptochat.views.ChatRoomListView
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavComposeApp() {
    val navController = rememberNavController()
    val actions = Action(navController)

    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null)
            AUTH_OPTION
        else
            AUTH_OPTION
    ) {
        composable(AUTH_OPTION) {
            AuthenticationView(
                register = actions.register,
                login = actions.login
            )
        }
        composable(REGISTER) {
            RegisterView(
                auth = actions.auth,
                back = actions.navigateBack
            )
        }
        composable(LOGIN) {
            LoginView(
                chatRooms = actions.chatRoomList,
                back = actions.navigateBack
            )
        }
        composable(CHATROOM_LIST) {
            ChatRoomListView(
                onChatRoomSelected = { roomName -> actions.chatRoom(roomName) }
            )
        }
        composable(
             CHATROOM,
//                arguments = listOf(navArgument("roomName") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomName = backStackEntry.arguments?.getString("roomName") ?: ""
            ChatView(
                roomName,
                onBackClick = actions.chatRoomList
            )
        }
    }
}
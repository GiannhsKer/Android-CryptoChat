package com.gi.cryptochat.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gi.cryptochat.view.authentication.AuthenticationView
import com.gi.cryptochat.view.chat.ChatView
import com.gi.cryptochat.view.chatroom.ChatRoomListView
import com.gi.cryptochat.view.login.LoginView
import com.gi.cryptochat.view.register.RegisterView
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavComposeApp() {
    val navController = rememberNavController()
    val actions = Action(navController)

    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null)
            Destination.AUTH_OPTION
        else
            Destination.AUTH_OPTION
    ) {
        composable(Destination.AUTH_OPTION) {
            AuthenticationView(
                register = actions.register,
                login = actions.login
            )
        }
        composable(Destination.REGISTER) {
            RegisterView(
                home = actions.auth,
                back = actions.navigateBack
            )
        }
        composable(Destination.LOGIN) {
            LoginView(
                home = actions.chatRoomList,
                back = actions.navigateBack
            )
        }
        composable(Destination.CHATROOM_LIST) {
            ChatRoomListView(
                onChatRoomSelected = { roomId -> actions.chatRoom(roomId) }
            )
        }
        composable(
            Destination.CHATROOM,
//                arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            ChatView(
                roomId,
                onBackClick = actions.chatRoomList // Pass navigation to chat room list
            )
        }
    }
}
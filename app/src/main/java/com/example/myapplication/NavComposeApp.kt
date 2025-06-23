package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.nav.Action
import com.example.myapplication.nav.Destination
import com.example.myapplication.nav.Destination.Login
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.view.AuthenticationView
import com.example.myapplication.view.AuthenticationView2
import com.example.myapplication.view.chatroom.ChatRoomListView
import com.example.myapplication.view.home.HomeView
import com.example.myapplication.view.login.LoginView
import com.example.myapplication.view.register.RegisterView
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavComposeApp() {
    val navController = rememberNavController()
    val actions = remember(navController) { Action(navController) }

    MyApplicationTheme {
        NavHost(
            navController = navController,
            startDestination =
            if (FirebaseAuth.getInstance().currentUser != null)
                Destination.ChatRoomList
            else
                Destination.AuthenticationOption
        ) {
            composable(Destination.AuthenticationOption) {
                AuthenticationView(
                    register = actions.register,
                    login = actions.login
                )
            }
            composable(Destination.Register) {
                RegisterView(
                    home = actions.home,
                    back = actions.navigateBack
                )
            }
            composable(Login) {
                LoginView(
                    home = actions.home,
                    back = actions.navigateBack
                )
            }
            composable(Destination.ChatRoomList) {
                ChatRoomListView(
                    onChatRoomSelected = { roomId -> actions.chatRoom(roomId) }
                )
            }
            composable(
                Destination.ChatRoom,
                arguments = listOf(navArgument("roomId") { type = NavType.StringType })
            ) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                HomeView(
                    roomId = roomId,
                    onBackClick = actions.chatRoomList // Pass navigation to chat room list
                )
            }
        }
    }
}

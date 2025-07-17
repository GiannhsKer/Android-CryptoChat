package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.nav.Action
import com.example.myapplication.nav.Destination
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.view.authentication.AuthenticationView
import com.example.myapplication.view.chatroom.ChatRoomListView
import com.example.myapplication.view.chat.HomeView
import com.example.myapplication.view.login.LoginView
import com.example.myapplication.view.register.RegisterView
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.view.chatroom.ChatRoomListViewModel

@Composable
fun NavComposeApp(chatRoomListViewModel: ChatRoomListViewModel = viewModel()) {
    val navController = rememberNavController()
    val actions = Action(navController)

    val chatRooms by chatRoomListViewModel.chatRooms.collectAsState()

    MyApplicationTheme {
        NavHost(
            navController = navController,
            startDestination = if (FirebaseAuth.getInstance().currentUser != null)
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
            composable(Destination.Login) {
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
                Destination.ChatRoom
//                arguments = listOf(navArgument("roomId") { type = NavType.StringType })
            ) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
//                val roomId = "Maths"
                HomeView(
                    roomId = roomId,
                    onBackClick = actions.chatRoomList // Pass navigation to chat room list
                )
            }
        }
    }
}

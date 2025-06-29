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
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.view.AuthenticationView
import com.example.myapplication.view.chatroom.ChatRoomListView
import com.example.myapplication.view.home.HomeView
import com.example.myapplication.view.login.LoginView
import com.example.myapplication.view.register.RegisterView
import com.google.firebase.auth.FirebaseAuth

import android.util.Log

@Composable
fun NavComposeApp() {
//    This creates and remembers a NavHostController — the object that manages app navigation
//    It should only be created once per Composable recomposition, which is what rememberNavController() does.
//    Without remember, a new NavController would be created every recomposition, breaking navigation.
    val navController = rememberNavController()
//    remember(...) { ... } caches a value and only re-executes the lambda if its key inputs change.
//    “Remember this Action(navController) as long as navController doesn’t change.”
    /*
    Why use remember(navController) { ... }?
        It ensures that: The Action object is only created once per navController
        If the navController changes (e.g., due to recomposition or scoping), it will recreate actions
        This is important in Compose, where things recompose frequently. You don’t want to recreate your navigation logic every time.
     */
    val actions = Action(navController)

    MyApplicationTheme {
        NavHost(
            navController = navController,
            startDestination = if (FirebaseAuth.getInstance().currentUser != null) Destination.ChatRoomList else Destination.AuthenticationOption
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

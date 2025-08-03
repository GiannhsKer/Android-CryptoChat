package com.gi.cryptochat.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gi.cryptochat.Constants.AUTHENTICATION_VIEW
import com.gi.cryptochat.R
import com.gi.cryptochat.SetStatusBarAppearance
import com.gi.cryptochat.viewmodels.AuthViewModel
import com.gi.cryptochat.gradientBrush
import kotlinx.coroutines.delay

@Composable
fun AuthenticationView(
    register: () -> Unit,
    login: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        authViewModel.snackbarMessage.collect { message ->
            Log.d(AUTHENTICATION_VIEW, "Snackbar Active")
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        SetStatusBarAppearance(
            useDarkIcons = true
        )
        Surface {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ){
                Image(
                    painterResource(id = R.drawable.cryptochat), contentDescription = "app logo",
                    modifier = Modifier.size(250.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(50))
                    ) {
                        OutlinedButton(
                            onClick = { register() },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Register")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                brush = gradientBrush
                            )
                    ) {
                        OutlinedButton(
                            onClick = { login() },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            border = BorderStroke(0.dp, Color.Transparent) // hide border if needed
                        ) {
                            Text("Login")
                        }
                    }
                }
            }
        }
    }
}
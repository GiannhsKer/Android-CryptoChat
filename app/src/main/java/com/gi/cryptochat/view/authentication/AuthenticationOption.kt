package com.gi.cryptochat.view.authentication

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gi.cryptochat.R
import com.gi.cryptochat.SetStatusBarAppearance
import com.gi.cryptochat.gradientBrush

@Composable
fun AuthenticationView(register: () -> Unit, login: () -> Unit) {

    SetStatusBarAppearance(
        useDarkIcons = true // or false if your background is dark
    )
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        )
        {
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
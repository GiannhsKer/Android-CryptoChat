package com.gi.cryptochat.view.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gi.cryptochat.getStatusBarHeight
import com.gi.cryptochat.gradientBrush
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(
    roomId: String,
    homeViewModel: ChatViewModel = viewModel(factory = ChatViewModel.provideFactory(roomId)),
    onBackClick: () -> Unit = {}
) {
    val message: String by homeViewModel.message.collectAsState(initial = "")
    val messages: List<Map<String, Any>> by homeViewModel.messages.collectAsState(
        initial = emptyList<Map<String, Any>>().toMutableList()
    )
    val messages2: List<ChatViewModel.ChatMessage> by homeViewModel.messages2.collectAsState(
        initial = emptyList<ChatViewModel.ChatMessage>().toMutableList()
    )

    // Define a list of modern, vibrant colors for user messages
    val userColors = listOf(
        Color(0xFF1ABC9C), // Turquoise
        Color(0xFF2ECC71), // Emerald
        Color(0xFF3498DB), // Peter River
        Color(0xFF9B59B6), // Amethyst
        Color(0xFF34495E), // Wet Asphalt
        Color(0xFFF1C40F), // Sun Flower
        Color(0xFFE67E22), // Carrot
        Color(0xFFE74C3C), // Alizarin
        Color(0xFF16A085), // Green Sea
        Color(0xFF2980B9), // Belize Hole
        Color(0xFF8E44AD), // Wisteria
        Color(0xFF2C3E50), // Midnight Blue
        Color(0xFFF39C12), // Orange
        Color(0xFFD35400), // Pumpkin
        Color(0xFFC0392B), // Pomegranate
        Color(0xFF27AE60), // Nephritis
        Color(0xFF2980B9), // Belize Hole (repeat for more users)
        Color(0xFF6C5CE7), // Bright Purple
        Color(0xFF00B894), // Greenish
        Color(0xFF00CEC9)  // Cyan
    )

    // Map user to color
    fun getUserColor(user: String): Color {
        val index = abs(user.hashCode()) % userColors.size
        return userColors[index]
    }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .height(56.dp + getStatusBarHeight())
                .background(brush = gradientBrush),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(top = getStatusBarHeight() + 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    roomId,
                    Modifier.weight(1f),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 0.85f, fill = false),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            reverseLayout = true
        ) {
            Log.d("Chat", messages2.toString())
//            items(messages, key = { it.getValue("sent_on") }) { message ->
//
//                val sentBy = message["sent_by_name"]?.toString() ?: message[Constants.SENT_BY]?.toString() ?: "Unknown"
//                SingleMessage(
//                    message = message[Constants.MESSAGE].toString(),
//                    isCurrentUser = message[Constants.IS_CURRENT_USER] as Boolean,
//                    sentBy = sentBy,
//                    sentOn = (message[Constants.SENT_ON] as? Number)?.toLong() ?: 0L,
//                    userColor = getUserColor(sentBy)
//                )
//            }
//            items(messages) { message ->
//                val isCurrentUser = message[Constants.IS_CURRENT_USER] as Boolean
//                val sentBy = message["sent_by_name"]?.toString()
//                    ?: message[Constants.SENT_BY]?.toString() ?: "Unknown"
//                val sentOn = (message[Constants.SENT_ON] as? Number)?.toLong() ?: 0L
//                val userColor = getUserColor(sentBy)
//
//                SingleMessage(
//                    message = message[Constants.MESSAGE].toString(),
//                    isCurrentUser = isCurrentUser,
//                    sentBy = sentBy,
//                    sentOn = sentOn,
//                    userColor = userColor
//                )
//            }
        }
        OutlinedTextField(
            value = message,
            onValueChange = { homeViewModel.updateMessage(it) },
            label = { Text("Type Your Message") },
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 1.dp)
                .fillMaxWidth()
                .weight(weight = 0.09f, fill = true),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        homeViewModel.addMessage()
                    },
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Button"
                    )
                }
            }
        )
    }
}
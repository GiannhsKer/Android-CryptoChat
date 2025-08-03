package com.gi.cryptochat.views

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gi.cryptochat.viewmodels.ChatMessage
import com.gi.cryptochat.viewmodels.ChatViewModel
import com.gi.cryptochat.getDateFromLong
import com.gi.cryptochat.getStatusBarHeight
import com.gi.cryptochat.gradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(
    roomName: String,
    chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.Companion.provideFactory(roomName)),
    onBackClick: () -> Unit = {}
) {
    val message: String by chatViewModel.message.collectAsState(initial = "")
    val messages: List<ChatMessage> by chatViewModel.messages.collectAsState(
        initial = emptyList<ChatMessage>().toMutableList()
    )

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
                BackArrowIcon(onBackClick = onBackClick)
                Text(
                    roomName,
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
            items(messages) { message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isCurrentUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        modifier = Modifier
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                            .widthIn(max = 280.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = chatViewModel.getUserColor(message.sentById)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                        ) {
                            Text(
                                text = message.message,
                                textAlign = if (message.isCurrentUser) TextAlign.End else TextAlign.Start,
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White,
                                fontSize = 17.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (message.isCurrentUser) Arrangement.End else Arrangement.Start
                            ) {
                                Text(
                                    text = "${message.sentByName} | ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )

                                Text(
                                    text = getDateFromLong(message.sentOn),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        OutlinedTextField(
            value = message,
            onValueChange = { chatViewModel.updateMessage(it) },
            label = { Text("Type Your Message") },
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 8.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = { chatViewModel.addMessage() },
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

@Composable
fun BackArrowIcon(onBackClick: () -> Unit = {}) {
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
}
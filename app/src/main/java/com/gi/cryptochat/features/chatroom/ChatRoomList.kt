package com.gi.cryptochat.features.chatroom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gi.cryptochat.AppAlertDialog
import com.gi.cryptochat.getDateFromLong
import com.gi.cryptochat.gradientBrush

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatRoomListView(
    onChatRoomSelected: (String) -> Unit,
    chatRoomListViewModel: ChatRoomListViewModel = viewModel()
) {
    val chatRooms by chatRoomListViewModel.chatRooms.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var chatRoomName by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    val uiState by chatRoomListViewModel.uiState.collectAsState()

    fun resetDialogState() {
        showDialog = false
        chatRoomName = ""
        errorText = ""
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Chat Rooms",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                Modifier.background(brush = gradientBrush),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .size(size = 56.dp)
                    .background(color = Color.Blue, shape = CircleShape),
                containerColor = Color.Transparent,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)

            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Chat Room",
                )
            }
        }
    ) { padding ->
        Text("Welcome $")
        LazyColumn(
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            items(chatRooms) { room ->
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onChatRoomSelected(room.chatRoomId) }
                        .clip(RoundedCornerShape(16.dp))
                        .drawWithCache {
                            onDrawBehind { drawRect(gradientBrush) }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                )
                {
                    Text(
                        room.chatRoomName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    Text(
                        "Created at ${getDateFromLong(room.createdAt)} by ${room.creatorName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.End)
                    )
                }
            }
        }
    }
    if (showDialog) {
        key(errorText) {
            AlertDialog(
                onDismissRequest = { resetDialogState() },
                title = { Text("Create Chat Room") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = chatRoomName,
                            onValueChange = { chatRoomName = it },
                            label = { Text("Room Name") }
                        )
                        if (errorText.isNotEmpty()) {
                            Text(
                                text = errorText,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        chatRoomListViewModel.createChatRoom(
                            chatRoomName,
                            onSuccess = { resetDialogState() },
                            onError = { error -> errorText = error }
                        )
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { resetDialogState() }) {
                        Text("Cancel")
                    }
                }
            )
        }
        uiState.error?.let { errorMessage ->
            AppAlertDialog(
                message = errorMessage,
                onDismiss = { chatRoomListViewModel.clearError() }
            )
        }
    }
}
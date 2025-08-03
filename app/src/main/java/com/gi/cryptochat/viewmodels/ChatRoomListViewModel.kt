package com.gi.cryptochat.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gi.cryptochat.Constants.CHATROOMLIST_VM
import com.gi.cryptochat.Constants.UNKNOWN_ROOM_NAME
import com.gi.cryptochat.Constants.UNKNOWN_DISPLAY_NAME
import com.gi.cryptochat.Constants.UNKNOWN_ID
import com.gi.cryptochat.UiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatRoom(
    val chatRoomName: String = "",
    val creatorName: String = "",
    val creatorId: String = "",
    val createdAt: Long = 0L
)

class ChatRoomListViewModel : ViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms

    private val db = Firebase.firestore

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _currentUserDisplayName = MutableStateFlow("")
    val currentUserDisplayName: StateFlow<String> = _currentUserDisplayName

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun snackbarSuccess(message: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    init {
        fetchChatRooms()
        _currentUserId.value = Firebase.auth.currentUser?.uid ?: UNKNOWN_ID
        _currentUserDisplayName.value = Firebase.auth.currentUser?.displayName ?: UNKNOWN_DISPLAY_NAME
    }

    fun fetchChatRooms() {
        db.collection("chatRooms").addSnapshotListener { snapshot, _ ->
            _chatRooms.value = snapshot?.documents?.map { doc ->
                snackbarSuccess("Login successful")
                val creatorName = if (doc.getString("creatorId") == currentUserId.value) {
                    "You"
                } else {
                    doc.getString("creatorName") ?: UNKNOWN_DISPLAY_NAME
                }
                ChatRoom(
                    chatRoomName = doc.getString("chatRoomName") ?: UNKNOWN_ROOM_NAME,
                    creatorName = creatorName,
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            } ?: emptyList()
        }
    }

    fun createChatRoom(chatRoomName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {

        val trimmedName = chatRoomName.trim().lowercase()
        if (trimmedName.isEmpty()) {
            Log.e(CHATROOMLIST_VM, "Room name is blank")
            onError("Room name cannot be empty")
            return
        }

        if (currentUserDisplayName.value == UNKNOWN_DISPLAY_NAME) {
            Log.e(CHATROOMLIST_VM, "Username not found in user document")
            onError("Username not found")
            return
        }

        db.collection("chatRooms").document(trimmedName).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    Log.e(CHATROOMLIST_VM, "Room name '$trimmedName' already exists")
                    onError("'$trimmedName' already exists")
                } else {
                    val chatRoom = ChatRoom(
                        creatorName = currentUserDisplayName.value,
                        creatorId = currentUserId.value,
                        chatRoomName = chatRoomName,
                        createdAt = System.currentTimeMillis()
                    )
                    db.collection("chatRooms").document(trimmedName)
                        .set(chatRoom)
                        .addOnSuccessListener {
                            onSuccess()
                        }.addOnFailureListener { error ->
                            Log.e(CHATROOMLIST_VM, "Failed to create chat room", error)
                            onError("Failed to create chat room")
                        }
                }
            }.addOnFailureListener { e ->
                Log.e(CHATROOMLIST_VM, "Failed to check room existence", e)
                onError("Failed to check if room exists")
            }
    }
}

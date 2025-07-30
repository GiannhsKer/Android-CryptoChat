package com.gi.cryptochat.features.chatroom

import android.util.Log
import androidx.lifecycle.ViewModel
import com.gi.cryptochat.Constants.CHATROOMLIST_VM
import com.gi.cryptochat.UiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ChatRoom(
    val chatRoomId: String = "",
    val chatRoomName: String = "",
    val creatorName: String = "",
    val creatorId: String = "",
    val createdAt: Long = 0L
)

class ChatRoomListViewModel : ViewModel() {
    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms

    private val db = Firebase.firestore
    val currentUserId = Firebase.auth.currentUser?.uid ?: "Unknown Id"

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchChatRooms()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun fetchChatRooms() {
        db.collection("chatRooms").addSnapshotListener { snapshot, _ ->
            _chatRooms.value = snapshot?.documents?.map { doc ->
                val creatorName = if (doc.getString("creatorId") == currentUserId) {
                    "You"
                } else {
                    doc.getString("creatorName") ?: "UnknownUser"
                }
                ChatRoom(
                    chatRoomId = doc.id,
                    chatRoomName = doc.getString("chatRoomName") ?: "UnknownUser",
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

        db.collection("users").document(currentUserId.toString()).get()
            .addOnSuccessListener { userDoc ->
                val username = userDoc.getString("username")
                if (username.isNullOrEmpty()) {
                    Log.e(CHATROOMLIST_VM, "Username not found in user document")
                    onError("Username not found")
                    return@addOnSuccessListener
                }

                db.collection("chatRooms").document(trimmedName).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            Log.e(CHATROOMLIST_VM, "Room name '$trimmedName' already exists")
                            onError("'$trimmedName' already exists")
                        } else {
                            val chatRoom = ChatRoom(
                                chatRoomId = doc.id,
                                creatorName = username,
                                creatorId = currentUserId,
                                chatRoomName = chatRoomName,
                                createdAt = System.currentTimeMillis()
                            )
                            db.collection("chatRooms").document(trimmedName)
                                .set(chatRoom)
                                .addOnSuccessListener {
                                    onSuccess()
                                }.addOnFailureListener { e ->
                                    Log.e(CHATROOMLIST_VM, "Failed to create chat room", e)
                                    onError("Failed to create chat room")
                                }
                        }
                    }.addOnFailureListener { e ->
                        Log.e(CHATROOMLIST_VM, "Failed to check room existence", e)
                        onError("Failed to check if room exists")
                    }
            }.addOnFailureListener { e ->
                Log.e(CHATROOMLIST_VM, "Failed to fetch user info", e)
                onError("Failed to fetch user info")
            }
    }
}

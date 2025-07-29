package com.gi.cryptochat.features.chatroom

import android.util.Log
import androidx.lifecycle.ViewModel
import com.gi.cryptochat.Constants.CHATROOMLIST_VM
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ChatRoom(
    val name: String,
    val creatorId: String,
    val creatorName: String,
    val createdAt: Long
)

data class ChatRoomListUiState(
    val loading: Boolean = false,
    val onSuccess: Boolean = false,
    val error: String? = null
)

class ChatRoomListViewModel : ViewModel() {
    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms

    private val db = Firebase.firestore
    val currentUserId = Firebase.auth.currentUser?.uid

    private val _uiState = MutableStateFlow(ChatRoomListUiState())
    val uiState: StateFlow<ChatRoomListUiState> = _uiState

    init {
        fetchChatRooms()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun fetchChatRooms() {
        db.collection("chatRooms").addSnapshotListener { snapshot, _ ->
            _chatRooms.value = snapshot?.documents?.map { doc ->
                val rawCreator = doc.getString("creator")
                val creatorId = when {
                    rawCreator == null -> "UnknownId"
                    rawCreator == currentUserId -> "You"
                    else -> rawCreator
                }
                Log.d(CHATROOMLIST_VM, "Document data : ${doc.data}")
                ChatRoom(
                    name = doc.id,
                    creatorId = creatorId,
                    creatorName = doc.getString("creatorName") ?: "UnknownName",
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            } ?: emptyList()
        }
    }

    fun createChatRoom(name: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (name.isBlank()) {
            Log.e(CHATROOMLIST_VM, "Room name is blank, calling onError")
            onError("Room name cannot be empty")
            return
        }
        val trimmedName = name.trim()

        val uid = currentUserId ?: "Unknown"
        if (uid == "Unknown") {
            onError("User not logged in")
            return
        }
        db.collection("users").document(uid).get().addOnSuccessListener { userDoc ->
            val username = userDoc.getString("username") ?: "Unknown"
            Log.d(CHATROOMLIST_VM, "Username is : $username")
            db.collection("chatRooms").document(trimmedName).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    Log.e(CHATROOMLIST_VM, "Room name already exists, calling onError")
                    onError("Room name already exists")
                } else {
                    db.collection("chatRooms").document(trimmedName).set(
                        ChatRoom(
                            name = uid,
                            creatorId = currentUserId.toString(),
                            creatorName = username,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError("Failed to create room") }
                }
            }
                .addOnFailureListener {
                    Log.e(CHATROOMLIST_VM,"Failed to check room name, calling onError",it) // Add this
                    onError("Failed to check room name")
                }
        }
    }
}

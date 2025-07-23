package com.gi.cryptochat.view.chatroom

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

import android.util.Log

class ChatRoomListViewModel : ViewModel() {
    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms

    private val db = Firebase.firestore

    init {
        fetchChatRooms()
    }

    fun fetchChatRooms() {
        db.collection("chatRooms").addSnapshotListener { chatRooms, _ ->
            _chatRooms.value = chatRooms?.documents?.map { chatRoom ->
                var creatorId = chatRoom.getString("creator") ?: "Unknown"
                if (creatorId != "Unknown")
                    // This is a blocking call, but for demo purposes, we use the id as fallback
                    // In production, you should cache usernames or denormalize
                    // Optionally, you could fetch username from users collection here
                    // But for now, just show the UID
                    creatorId = if (creatorId == Firebase.auth.currentUser?.uid) "You" else creatorId
                ChatRoom(chatRoom.id, creatorId)
            } ?: emptyList()
        }
    }

    fun createChatRoom(name: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (name.isBlank()) {
            Log.e("ChatRoomVM", "Room name is blank, calling onError") // Add this
            onError("Room name cannot be empty")
            return
        }
        val trimmed = name.trim()
        val currentUser = Firebase.auth.currentUser
        val uid = currentUser?.uid ?: "Unknown"
        if (uid == "Unknown") {
            onError("User not logged in")
            return
        }
        db.collection("users").document(uid).get().addOnSuccessListener { userDoc ->
            val username = userDoc.getString("username") ?: "Unknown"
            db.collection("chatRooms").document(trimmed).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    Log.e("ChatRoomVM", "Room name already exists, calling onError") // Add this
                    onError("Room name already exists")
                } else {
                    db.collection("chatRooms").document(trimmed).set(
                        hashMapOf(
                            "createdAt" to System.currentTimeMillis(),
                            "creator" to username
                        )
                    )
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError("Failed to create room") }
                }
            }.addOnFailureListener {
                Log.e("ChatRoomVM", "Failed to check room name, calling onError", it) // Add this
                onError("Failed to check room name") }
        }.addOnFailureListener { onError("Failed to fetch user info") }
    }
}

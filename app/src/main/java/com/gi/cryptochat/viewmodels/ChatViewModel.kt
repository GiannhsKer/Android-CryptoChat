package com.gi.cryptochat.viewmodels

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gi.cryptochat.Constants.CHAT_VM
import com.gi.cryptochat.Constants.UNKNOWN_DISPLAY_NAME
import com.gi.cryptochat.Constants.UNKNOWN_ID
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.Exclude
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

data class ChatMessage(
    val message: String = "",
    val sentById: String = "",
    val sentByName: String = "",
    val sentOn: Long = 0L
) {
    @get:Exclude // Exclude from Firebase serialization
    var isCurrentUser: Boolean = false
}

class ChatViewModel(private val roomName: String) : ViewModel() {

    init {
        fetchMessages()
    }

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

    fun getUserColor(user: String): Color {
        val index = abs(user.hashCode()) % userColors.size
        return userColors[index]
    }

    private val db = Firebase.firestore
    val currentUserId = Firebase.auth.currentUser?.uid ?: UNKNOWN_ID
    val currentUserDisplayName = Firebase.auth.currentUser?.displayName ?: UNKNOWN_DISPLAY_NAME

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private var _messages = MutableStateFlow(emptyList<ChatMessage>().toMutableList())
    val messages: StateFlow<MutableList<ChatMessage>> = _messages

    fun updateMessage(message: String) {
        _message.value = message
    }

    private fun updateMessages(list: MutableList<ChatMessage>) {
        _messages.value = list.asReversed()
    }

    fun addMessage() {
        db.collection("chatRooms").document(roomName).collection("messages")
            .document()
            .set(
                ChatMessage(
                    _message.value,
                    currentUserId,
                    currentUserDisplayName,
                    System.currentTimeMillis()
                )
            )
            .addOnSuccessListener {
                _message.value = ""
            }
            .addOnFailureListener { error ->
                Log.e(CHAT_VM, "Failed to send message", error)
            }
    }


    private fun fetchMessages() {
        Firebase.firestore.collection("chatRooms").document(roomName).collection("messages")
            .orderBy("sentOn").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(CHAT_VM, "Listen failed.", error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { document ->
                    val message = document.toObject(ChatMessage::class.java)
                    message?.apply {
                        isCurrentUser = currentUserId == sentById
                    }
                }?.toMutableList() ?: mutableListOf()
                updateMessages(messages)
            }
    }

    fun clearCacheAndLogout() {
        // Sign out from Firebase
        Firebase.auth.signOut()
        // TODO: Add cache clearing logic if you use any local storage (e.g., SharedPreferences, Room, etc.)
        // Navigate to authentication page
        // This should be handled in the Composable via a callback or navigation event
    }

    companion object {
        fun provideFactory(roomName: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return ChatViewModel(roomName) as T
                }
            }
    }
}
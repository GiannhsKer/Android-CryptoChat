package com.gi.cryptochat.view.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gi.cryptochat.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.Exclude
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ChatViewModel(private val roomId: String) : ViewModel() {

    val CHATVIEWMODEL = "ChatViewModel"

    init {
        getMessages()
    }

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private var _messages = MutableStateFlow(emptyList<Map<String, Any>>().toMutableList())
    val messages: StateFlow<MutableList<Map<String, Any>>> = _messages

    private var _messages2 = MutableStateFlow(emptyList<ChatMessage>().toMutableList())
    val messages2: StateFlow<MutableList<ChatMessage>> = _messages2


    data class ChatMessage(
        val message: String,
        val sentBy: String,
        val sentByName: String,
        val sentOn: Long
    ) {
        @get:Exclude // Exclude from Firebase serialization
        var isCurrentUser: Boolean = false
    }

    fun updateMessage(message: String) {
        _message.value = message
    }

    fun addMessage() {
        val message: String =
            _message.value ?: throw IllegalArgumentException("message empty")
        val uid = Firebase.auth.currentUser?.uid
        if (message.isNotEmpty() && uid != null) {
            val db = Firebase.firestore
            db.collection("users").document(uid).get().addOnSuccessListener { document ->
                val username = document.getString("username") ?: "Unknown"
                db.collection("chatRooms").document(roomId).collection("messages").document().set(
                    hashMapOf(
                        Constants.MESSAGE to message,
                        Constants.SENT_BY to uid,
                        "sent_by_name" to username,
                        Constants.SENT_ON to System.currentTimeMillis()
                    )
                ).addOnSuccessListener {
                    _message.value = ""
                }
            }
        }
    }


    private fun getMessages() {
        val currentUserId = Firebase.auth.currentUser?.uid.orEmpty()

        Firebase.firestore
            .collection("chatRooms")
            .document(roomId)
            .collection("messages")
            .orderBy(Constants.SENT_ON)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(CHATVIEWMODEL, "Listen failed.", error)
                    return@addSnapshotListener
                }

                val messages2 = snapshot?.documents?.mapNotNull { document ->
                    Log.d(CHATVIEWMODEL, "try to get data")

//                    Log.d(CHATVIEWMODEL, document.toObject(ChatMessage::class.java).toString())


                    val messageD = document.toObject(ChatMessage::class.java)
                    Log.d(CHATVIEWMODEL, "got data")
                    messageD?.apply {
                        isCurrentUser = currentUserId == ""
                    }

                }?.toMutableList() ?: mutableListOf()
                Log.d(CHATVIEWMODEL, messages2.toString())

                updateMessages2(messages2)
            }

//        Firebase.firestore
//            .collection("chatRooms")
//            .document(roomId)
//            .collection("messages")
//            .orderBy(Constants.SENT_ON)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    Log.e("ChatViewModel", "Listen failed.", error)
//                    return@addSnapshotListener
//                }
//
//                val messages = snapshot?.mapNotNull { document ->
//                    Log.d(CHATVIEWMODEL+"2", document.toString())
//                    val mutableData = document.data.toMutableMap()
//
//                    if (!mutableData.containsKey(Constants.SENT_BY) || !mutableData.containsKey(Constants.MESSAGE))
//                        return@mapNotNull null
//
//                    mutableData[Constants.IS_CURRENT_USER] = currentUserId == mutableData[Constants.SENT_BY].toString()
//
//                    mutableData.toMap()
//                } ?.toMutableList() ?: mutableListOf()
//
//                updateMessages(messages)
//            }
    }

    private fun updateMessages(list: MutableList<Map<String, Any>>) {
        _messages.value = list.asReversed()
    }
    private fun updateMessages2(list: MutableList<ChatMessage>) {
        _messages2.value = list.asReversed()
    }

    fun clearCacheAndLogout() {
        // Sign out from Firebase
        Firebase.auth.signOut()
        // TODO: Add cache clearing logic if you use any local storage (e.g., SharedPreferences, Room, etc.)
        // Navigate to authentication page
        // This should be handled in the Composable via a callback or navigation event
    }

    companion object {
        fun provideFactory(roomId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return ChatViewModel(roomId) as T
                }
            }
    }
}
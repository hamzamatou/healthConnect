package com.example.healthproject.data.repository

import com.example.healthproject.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore

class MessageRepository {

    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(message: Message, callback: (Boolean, String?) -> Unit) {
        val docRef = db.collection("messages").document()
        val newMessage = message.copy(id = docRef.id)
        docRef.set(newMessage)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun getMessagesByMission(missionId: String, callback: (List<Message>) -> Unit) {
        db.collection("messages")
            .whereEqualTo("missionId", missionId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(Message::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }
}

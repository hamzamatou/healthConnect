package com.example.healthproject.data.repository

import android.util.Log
import com.example.healthproject.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore

class MessageRepository {

    private val db = FirebaseFirestore.getInstance()

    // Envoi d'un message avec timestamp
    fun sendMessage(message: Message, callback: (Boolean, String?) -> Unit) {
        val docRef = db.collection("messages").document()
        val newMessage = message.copy(id = docRef.id)
        docRef.set(newMessage)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }


    // Écoute en temps réel des messages
    fun listenMessagesByMission(
        missionId: String,
        callback: (List<Message>) -> Unit
    ) {
        db.collection("messages")
            .whereEqualTo("missionId", missionId)
            .orderBy("timestamp") // 🔹 Firestore nécessite un index ici
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MessageRepo", "Erreur écoute messages: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    callback(snapshot.toObjects(Message::class.java))
                } else {
                    callback(emptyList())
                }
            }
    }


    // Récupérer tous les messages existants (optionnel)
    fun getMessagesByMission(
        missionId: String,
        callback: (List<Message>) -> Unit
    ) {
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

package com.example.healthproject.data.repository

import com.example.healthproject.data.model.Participation
import com.google.firebase.firestore.FirebaseFirestore

class ParticipationRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getParticipationsByParticipant(
        participantId: String,
        callback: (List<Participation>) -> Unit
    ) {
        db.collection("participations")
            .whereEqualTo("participantId", participantId)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(Participation::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }

    fun addParticipation(participation: Participation, callback: (Boolean, String?) -> Unit) {
        val docRef = db.collection("participations").document()
        val newParticipation = participation.copy(id = docRef.id)
        docRef.set(newParticipation)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }
}

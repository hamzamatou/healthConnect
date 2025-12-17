package com.example.healthproject.data.repository

import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.DemandeStatus
import com.google.firebase.firestore.FirebaseFirestore

class DemandeParticipationRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getDemandesByParticipantAndStatus(
        participantId: String,
        status: DemandeStatus,
        callback: (List<DemandeParticipation>) -> Unit
    ) {
        db.collection("demandesParticipation")
            .whereEqualTo("participantId", participantId)
            .whereEqualTo("status", status.name)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(DemandeParticipation::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }

    fun getDemandesByMissionAndStatus(
        missionId: String,
        status: DemandeStatus,
        callback: (List<DemandeParticipation>) -> Unit
    ) {
        db.collection("demandesParticipation")
            .whereEqualTo("missionId", missionId)
            .whereEqualTo("status", status.name)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(DemandeParticipation::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }

    fun createDemande(demande: DemandeParticipation, callback: (Boolean, String?) -> Unit) {
        val docRef = db.collection("demandesParticipation").document()
        val newDemande = demande.copy(id = docRef.id)
        docRef.set(newDemande)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun updateDemandeStatus(demandeId: String, status: DemandeStatus, callback: (Boolean, String?) -> Unit) {
        db.collection("demandesParticipation").document(demandeId)
            .update("status", status.name)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }
}

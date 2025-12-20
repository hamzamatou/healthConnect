package com.example.healthproject.data.repository

import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.DemandeStatus
import com.google.firebase.firestore.FirebaseFirestore

class DemandeParticipationRepository {

    private val db = FirebaseFirestore.getInstance()

    // Récupérer toutes les demandes d'un participant selon le statut
    fun getDemandesByParticipantAndStatus(
        userId: String,
        status: DemandeStatus,
        callback: (List<DemandeParticipation>) -> Unit
    ) {
        db.collection("demandesParticipation")
            .whereEqualTo("userId", userId)
            .whereEqualTo("statut", status.name)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(DemandeParticipation::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }

    // Récupérer toutes les demandes pour une mission et éventuellement par statut
    fun getDemandesByMissionAndStatus(
        missionId: String,
        status: DemandeStatus? = null,
        callback: (List<DemandeParticipation>) -> Unit
    ) {
        val collection = db.collection("demandesParticipation")
        val query = if (status != null) {
            collection.whereEqualTo("missionId", missionId)
                .whereEqualTo("statut", status.name)
        } else {
            collection.whereEqualTo("missionId", missionId)
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(DemandeParticipation::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }

    // Créer une nouvelle demande
    fun createDemande(demande: DemandeParticipation, callback: (Boolean, String?) -> Unit) {
        val docRef = db.collection("demandesParticipation").document()
        val newDemande = demande.copy(id = docRef.id)
        docRef.set(newDemande)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    // Mettre à jour le statut et éventuellement la raison du refus
    fun updateDemandeStatus(
        demandeId: String,
        status: DemandeStatus,
        raisonRefus: String? = null,
        callback: (Boolean, String?) -> Unit
    ) {
        val updateMap = mutableMapOf<String, Any>(
            "statut" to status.name
        )
        raisonRefus?.let { updateMap["raisonRefus"] = it }

        db.collection("demandesParticipation").document(demandeId)
            .update(updateMap)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    // Récupérer une demande pour un utilisateur et une mission
    fun getDemandeByMissionAndUser(
        missionId: String,
        userId: String,
        callback: (DemandeParticipation?) -> Unit
    ) {
        db.collection("demandesParticipation")
            .whereEqualTo("missionId", missionId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(DemandeParticipation::class.java).firstOrNull())
            }
            .addOnFailureListener { _ -> callback(null) }
    }

    // --- Méthodes pour gérer la présence ---
    fun getParticipantsForMission(
        missionId: String,
        callback: (List<DemandeParticipation>) -> Unit
    ) {
        db.collection("demandesParticipation")
            .whereEqualTo("missionId", missionId)
            .whereIn(
                "statut",
                listOf(
                    DemandeStatus.ACCEPTEE.name,
                    DemandeStatus.PRESENT.name,
                    DemandeStatus.ABSENT.name
                )
            )
            .get()
            .addOnSuccessListener {
                callback(it.toObjects(DemandeParticipation::class.java))
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun updatePresence(demandeId: String, present: Boolean) {
        val newStatus = if (present) DemandeStatus.PRESENT else DemandeStatus.ABSENT
        db.collection("demandesParticipation")
            .document(demandeId)
            .update("statut", newStatus.name)
    }
    // Mettre à jour la présence
    fun markPresence(demandeId: String, present: Boolean, callback: (Boolean) -> Unit) {
        val newStatus = if (present) DemandeStatus.PRESENT else DemandeStatus.ABSENT
        db.collection("demandesParticipation")
            .document(demandeId)
            .update("statut", newStatus.name)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}

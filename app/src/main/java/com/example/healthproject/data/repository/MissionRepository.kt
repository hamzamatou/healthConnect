package com.example.healthproject.data.repository

import com.example.healthproject.data.model.Mission
import com.example.healthproject.data.model.MissionStatus
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class MissionRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAllMissions(callback: (List<Mission>) -> Unit) {
        db.collection("missions")
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(Mission::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }

    fun getMissionsByIds(missionIds: List<String>, callback: (List<Mission>) -> Unit) {
        if (missionIds.isEmpty()) {
            callback(emptyList())
            return
        }
        db.collection("missions")
            .whereIn(FieldPath.documentId(), missionIds)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(Mission::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }

    fun createMission(mission: Mission, callback: (Boolean, String?) -> Unit) {
        val docRef = db.collection("missions").document()
        val newMission = mission.copy(id = docRef.id)
        docRef.set(newMission)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }
    fun getMissionById(missionId: String, callback: (Mission?) -> Unit) {
        db.collection("missions").document(missionId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    callback(snapshot.toObject(Mission::class.java))
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { _ -> callback(null) }
    }
    fun updateMissionStatus(missionId: String, status: MissionStatus) {
        db.collection("missions")
            .document(missionId)
            .update("statut", status)
            .addOnSuccessListener {
                // Optionnel : log ou toast pour confirmer
                println("Mission $missionId mise à jour à CLOTUREE")
            }
            .addOnFailureListener { e ->
                println("Erreur lors de la mise à jour : ${e.message}")
            }
    }



}

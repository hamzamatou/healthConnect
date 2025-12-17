package com.example.healthproject.data.repository

import com.example.healthproject.data.model.Mission
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
}

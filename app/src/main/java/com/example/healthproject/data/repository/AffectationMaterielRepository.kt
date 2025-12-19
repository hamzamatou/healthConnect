package com.example.healthproject.data.repository

import com.example.healthproject.data.model.AffectationMateriel
import com.google.firebase.firestore.FirebaseFirestore

class AffectationMaterielRepository {

    private val db = FirebaseFirestore.getInstance()

    fun affecterMateriel(
        affectation: AffectationMateriel,
        callback: (Boolean, String?) -> Unit
    ) {
        val docRef = db.collection("affectationsMateriel").document()
        val newAffectation = affectation.copy(id = docRef.id)
        docRef.set(newAffectation)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun getAffectationsByMission(
        missionId: String,
        callback: (List<AffectationMateriel>) -> Unit
    ) {
        db.collection("affectationsMateriel")
            .whereEqualTo("missionId", missionId)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObjects(AffectationMateriel::class.java))
            }
            .addOnFailureListener { _ -> callback(emptyList()) }
    }
    fun updateEtatApres(id: String, etat: String) {
        db.collection("affectationsMateriel")
            .document(id)
            .update("etatApres", etat)
    }
}

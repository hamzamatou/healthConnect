package com.example.healthproject.data.model

import com.google.firebase.firestore.DocumentId

data class DemandeParticipation(
    @DocumentId
    val id: String? = null,

    val missionId: String = "",
    val userId: String = "",

    val roleMission: RoleMission = RoleMission.VOLONTAIRE,

    val specialite: String? = null,        // médecin
    val profession: String? = null,        // superviseur
    val caracteristiques: String? = null,  // superviseur

    val statut: DemandeStatus = DemandeStatus.EN_ATTENTE,
    val dateDemande: Long = System.currentTimeMillis(),
    val raisonRefus: String? = null
)

package com.example.healthproject.data.model

import com.google.firebase.firestore.DocumentId

data class Mission(
    @DocumentId
    val id: String? = null,

    val titre: String = "",
    val description: String = "",
    val dateDebut: Long = 0L,
    val dateFin: Long = 0L,
    val lieu: String = "",
    val statut: MissionStatus = MissionStatus.OUVERTE,
    val nbrMedecin: Int = 0,
    val nbrInfirmier: Int = 0,
    val nbrVolontaire: Int = 0,
    val imageBase64: String? = null // <- nouveau champ

)

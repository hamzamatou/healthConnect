package com.example.healthproject.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String? = null,
    val cin: String = "",
    val nom: String = "",
    val prenom: String = "",
    val email: String = "",
    val adresse: String = "",
    val numeroTelephone: String = "",

    val type: UserType = UserType.PARTICIPANT
)

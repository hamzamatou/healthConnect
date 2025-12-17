package com.example.healthproject.data.model

import com.google.firebase.firestore.DocumentId

data class Materiel(
    @DocumentId
    val id: String? = null,

    val nom: String = "",
    val description: String = "",
    val etat: String = "DISPONIBLE",
    val quantiteInitiale: Int = 0,
    val reutilisable: Boolean = true
)

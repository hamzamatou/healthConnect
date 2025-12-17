package com.example.healthproject.data.model

import com.google.firebase.firestore.DocumentId

data class AffectationMateriel(
    @DocumentId
    val id: String? = null,

    val missionId: String = "",
    val materielId: String = "",
    val quantiteAffectee: Int = 0,
    val etatAvant: String = "",
    val etatApres: String? = null
)

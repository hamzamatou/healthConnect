package com.example.healthproject.data.model

import com.google.firebase.firestore.DocumentId

data class Participation(
    @DocumentId
    val id: String? = null,

    val missionId: String = "",
    val userId: String = "",

    val roleMission: RoleMission = RoleMission.VOLONTAIRE,

    val present: Boolean? = null
)

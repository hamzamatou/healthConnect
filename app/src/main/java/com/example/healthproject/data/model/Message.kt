package com.example.healthproject.data.model

import com.google.firebase.firestore.DocumentId

data class Message(
    @DocumentId
    val id: String? = null,

    val missionId: String = "",
    val senderId: String = "",
    val contenu: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

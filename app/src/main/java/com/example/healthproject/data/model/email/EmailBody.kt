package com.example.healthproject.data.model.email


data class EmailBody(
    val sender: Sender,
    val to: List<Recipient>,
    val subject: String,
    val textContent: String
)
data class Sender(val name: String, val email: String)
data class Recipient(val email: String)
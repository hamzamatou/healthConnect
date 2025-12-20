package com.example.healthproject.ui.pharmaassistant

// ====== Requête Groq ======
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double
)

data class GroqMessage(
    val role: String,
    val content: String
)

// ====== Réponse Groq ======
data class GroqResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: GroqMessage
)

// ====== Réponse Pharmaceutique finale ======
data class MedicamentInfo(
    val medicament_nom: String,
    val description_courte: String,
    val substances_actives: List<String>,
    val mode_utilisation: String,
    val avertissement: String
)

package com.example.healthproject.data.repository

import com.example.healthproject.ui.pharmaassistant.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PharmaAssistantRepository {

    private val SYSTEM_PROMPT = """
Tu es un assistant d'information pharmaceutique rapide et factuel, dédié aux professionnels de la santé.
Réponds STRICTEMENT au format JSON :
{"medicament_nom": "...", "description_courte": "...", "substances_actives": [], "mode_utilisation": "..."}
Si l'information est introuvable, remplis avec "Information non disponible".
""".trimIndent()

    fun askMedication(
        medicationName: String,
        callback: (String) -> Unit
    ) {
        val request = GroqRequest(
            model = "llama-3.3-70b-versatile",
            temperature = 0.1,
            messages = listOf(
                GroqMessage("system", SYSTEM_PROMPT),
                GroqMessage("user", "Fournis les informations pour : $medicationName")
            )
        )

        RetrofitClient.api.getCompletion(request)
            .enqueue(object : Callback<GroqResponse> {

                override fun onResponse(
                    call: Call<GroqResponse>,
                    response: Response<GroqResponse>
                ) {
                    val rawText = response.body()
                        ?.choices
                        ?.firstOrNull()
                        ?.message
                        ?.content

                    callback(parseAndFormat(rawText, medicationName))
                }

                override fun onFailure(call: Call<GroqResponse>, t: Throwable) {
                    callback("Erreur réseau : ${t.message}")
                }
            })
    }

    private fun parseAndFormat(text: String?, name: String): String {
        if (text == null) return "Aucune réponse"

        return try {
            val json = JSONObject(text)
            """
💊 Médicament : ${json.optString("medicament_nom", name)}

📌 Description :
${json.optString("description_courte")}

🧪 Substances actives :
${json.optJSONArray("substances_actives")}

📝 Mode d'utilisation :
${json.optString("mode_utilisation")}

⚠️ Ceci est une information générée par IA. À vérifier (RCP).
""".trimIndent()
        } catch (e: Exception) {
            "Erreur de format. Vérifiez l’orthographe du médicament."
        }
    }
}

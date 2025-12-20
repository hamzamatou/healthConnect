/*package com.example.healthproject.data.repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID
import com.example.healthproject.ui.Chatboot.DialogflowRequest
import com.example.healthproject.ui.Chatboot.QueryInput
import com.example.healthproject.ui.Chatboot.TextInput
import com.example.healthproject.ui.Chatboot.DialogflowResponse
import com.example.healthproject.ui.Chatboot.RetrofitClient

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun sendMessage(message: String, onResult: (String) -> Unit) {
        val sessionId = UUID.randomUUID().toString()
        val request = DialogflowRequest(QueryInput(TextInput(message)))

       RetrofitClient.dialogflowApi.detectIntent(request(message))
            .enqueue(object : Callback<DialogflowResponse> {
                override fun onResponse(call: Call<DialogflowResponse>, response: Response<DialogflowResponse>) {
                    if (response.isSuccessful) {
                        val dfResponse = response.body()
                        val mission = dfResponse?.queryResult?.parameters?.get("mission")
                        if (!mission.isNullOrEmpty()) {
                            checkMission(mission, onResult)
                        } else {
                            onResult("Je n'ai pas compris la mission.")
                        }
                    } else onResult("Erreur Dialogflow")
                }

                override fun onFailure(call: Call<DialogflowResponse>, t: Throwable) {
                    onResult("Connexion impossible")
                }
            })
    }

    private fun checkMission(mission: String, onResult: (String) -> Unit) {
        db.collection("missions").document(mission).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val participants = doc.get("participants") as? List<String> ?: emptyList()
                    if (participants.contains(userId)) {
                        generateAttestation(mission, onResult)
                    } else onResult("Vous ne participez pas à cette mission.")
                } else onResult("Mission introuvable.")
            }
            .addOnFailureListener { onResult("Erreur Firestore") }
    }

    private fun generateAttestation(mission: String, onResult: (String) -> Unit) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val attestation = "Attestation pour la mission $mission\nNom: ...\nPrénom: ..."
        // Ici tu peux utiliser JavaMail pour envoyer l’email
        onResult("Attestation générée et envoyée à $userEmail")
    }
}
*/
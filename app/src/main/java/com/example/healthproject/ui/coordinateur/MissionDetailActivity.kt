package com.example.healthproject.ui.coordinateur

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.model.*
import com.example.healthproject.data.model.email.*
import com.example.healthproject.databinding.ActivityMissionDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MissionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMissionDetailBinding
    private var isEditing = false
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val missionId = intent.getStringExtra("MISSION_ID")
        if (missionId == null) {
            Toast.makeText(this, "Erreur : ID Mission manquant", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val missionRef = db.collection("missions").document(missionId)

        loadMissionDetails(missionRef)

        // FAB Modifier / Enregistrer
        binding.fabEditMission.setOnClickListener {
            if (isEditing) {
                saveMissionChanges(missionRef)
            } else {
                isEditing = true
                setEditingMode(true)
                Toast.makeText(this, "Mode édition activé", Toast.LENGTH_SHORT).show()
            }
        }

        // FAB Annuler mission
        binding.fabCancelMission.setOnClickListener {
            cancelMission(missionRef, missionId)
        }

        binding.btnViewParticipants.setOnClickListener {
            startActivity(Intent(this, ParticipantsActivity::class.java).apply {
                putExtra("MISSION_ID", missionId)
            })
        }

        binding.btnViewAllRequests.setOnClickListener {
            startActivity(Intent(this, AllRequestsActivity::class.java).apply {
                putExtra("MISSION_ID", missionId)
            })
        }
    }

    private fun loadMissionDetails(missionRef: DocumentReference) {
        missionRef.get().addOnSuccessListener { doc ->
            val mission = doc.toObject(Mission::class.java)
            mission?.let {
                binding.apply {
                    editTextMissionName.setText(it.titre)
                    editTextMissionDescription.setText(it.description)
                    editTextMissionLieu.setText(it.lieu)
                    editTextMissionDateDebut.setText(it.dateDebut.toString())
                    editTextMissionDateFin.setText(it.dateFin.toString())
                    editTextMissionStatut.setText(it.statut.name)
                    editTextMissionNbrMedecin.setText(it.nbrMedecin.toString())
                    editTextMissionNbrInfirmier.setText(it.nbrInfirmier.toString())
                    editTextMissionNbrVolontaire.setText(it.nbrVolontaire.toString())

                    // Désactiver annulation si déjà fait
                    if (it.statut == MissionStatus.ANNULE) {
                        fabCancelMission.isEnabled = false
                        fabCancelMission.alpha = 0.5f
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.e("MissionDetail", "Erreur chargement", e)
        }
    }

    private fun setEditingMode(enabled: Boolean) {
        binding.apply {
            editTextMissionName.isEnabled = enabled
            editTextMissionDescription.isEnabled = enabled
            editTextMissionLieu.isEnabled = enabled
            editTextMissionDateDebut.isEnabled = enabled
            editTextMissionDateFin.isEnabled = enabled
            editTextMissionStatut.isEnabled = false // Le statut ne devrait pas être modifié manuellement ici
            editTextMissionNbrMedecin.isEnabled = enabled
            editTextMissionNbrInfirmier.isEnabled = enabled
            editTextMissionNbrVolontaire.isEnabled = enabled

            // Changer l'icône du FAB selon l'état (optionnel)
            // fabEditMission.setImageResource(if (enabled) R.drawable.ic_save else R.drawable.ic_edit)
        }
    }

    private fun saveMissionChanges(missionRef: DocumentReference) {
        val updatedMission = mapOf(
            "titre" to binding.editTextMissionName.text.toString(),
            "description" to binding.editTextMissionDescription.text.toString(),
            "lieu" to binding.editTextMissionLieu.text.toString(),
            "dateDebut" to (binding.editTextMissionDateDebut.text.toString().toLongOrNull() ?: 0L),
            "dateFin" to (binding.editTextMissionDateFin.text.toString().toLongOrNull() ?: 0L),
            "nbrMedecin" to (binding.editTextMissionNbrMedecin.text.toString().toIntOrNull() ?: 0),
            "nbrInfirmier" to (binding.editTextMissionNbrInfirmier.text.toString().toIntOrNull() ?: 0),
            "nbrVolontaire" to (binding.editTextMissionNbrVolontaire.text.toString().toIntOrNull() ?: 0)
        )

        missionRef.update(updatedMission)
            .addOnSuccessListener {
                isEditing = false
                setEditingMode(false)
                Toast.makeText(this, "Mission mise à jour !", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur mise à jour", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancelMission(missionRef: DocumentReference, missionId: String) {
        missionRef.update("statut", MissionStatus.ANNULE.name)
            .addOnSuccessListener {
                Toast.makeText(this, "Mission annulée !", Toast.LENGTH_SHORT).show()
                binding.editTextMissionStatut.setText(MissionStatus.ANNULE.name)
                sendEmailToConfirmedParticipants(missionId)
            }
            .addOnFailureListener { e ->
                Log.e("MissionDetail", "Erreur annulation", e)
            }
    }

    private fun sendEmailToConfirmedParticipants(missionId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.brevo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(BrevoApi::class.java)
        val apiKey = "apikey" // Ne laissez pas traîner votre clé en clair en production

        db.collection("demandesParticipation")
            .whereEqualTo("missionId", missionId)
            .whereEqualTo("statut", DemandeStatus.ACCEPTEE.name)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    val userId = doc.getString("userId") ?: continue

                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { userDoc ->
                            val email = userDoc.getString("email") ?: return@addOnSuccessListener

                            val body = EmailBody(
                                sender = Sender("HealthApp", "nourkalay21@gmail.com"),
                                to = listOf(Recipient(email)),
                                subject = "Annulation de mission",
                                textContent = "Bonjour, nous vous informons que la mission à laquelle vous étiez inscrit a été annulée."
                            )

                            service.sendEmail(body, apiKey).enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    if(response.isSuccessful) Log.d("Email", "Succès pour $email")
                                    else Log.e("Email", "Erreur API: ${response.code()}")
                                }
                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    Log.e("Email", "Échec réseau", t)
                                }
                            })
                        }
                }
            }
    }
}

package com.example.healthproject.ui.coordinateur

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.data.model.Mission
import com.example.healthproject.data.model.MissionStatus
import com.example.healthproject.databinding.ActivityMissionDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference


class MissionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMissionDetailBinding
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val missionId = intent.getStringExtra("MISSION_ID")
        if (missionId == null) return

        val missionRef = FirebaseFirestore.getInstance().collection("missions").document(missionId)

        // Charger les détails de la mission
        missionRef.get().addOnSuccessListener { doc ->
            val mission = doc.toObject(Mission::class.java)
            mission?.let {
                binding.editTextMissionName.setText(it.titre)
                binding.editTextMissionDescription.setText(it.description)
                binding.editTextMissionLieu.setText(it.lieu)
                binding.editTextMissionDateDebut.setText(it.dateDebut.toString())
                binding.editTextMissionDateFin.setText(it.dateFin.toString())
                binding.editTextMissionStatut.setText(it.statut.name)
                binding.editTextMissionNbrMedecin.setText(it.nbrMedecin.toString())
                binding.editTextMissionNbrInfirmier.setText(it.nbrInfirmier.toString())
                binding.editTextMissionNbrVolontaire.setText(it.nbrVolontaire.toString())
            }
        }

        // FAB Modifier / Enregistrer
        binding.fabEditMission.setOnClickListener {
            isEditing = !isEditing
            setEditingMode(isEditing)

            if (!isEditing) saveMissionChanges(missionRef)
            else Toast.makeText(this, "Mode édition activé", Toast.LENGTH_SHORT).show()
        }

        // FAB Annuler mission
        binding.fabCancelMission.setOnClickListener {
            missionRef.update("statut", MissionStatus.ANNULE.name)
                .addOnSuccessListener {
                    Toast.makeText(this, "Mission annulée !", Toast.LENGTH_SHORT).show()
                    Log.d("MissionDetail", "Mission annulée avec succès")
                    sendEmailToConfirmedParticipants(missionId)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur lors de l'annulation", Toast.LENGTH_SHORT).show()
                    Log.e("MissionDetail", "Erreur annulation", e)
                }
        }

        // Bouton voir participants
        binding.btnViewParticipants.setOnClickListener {
            val intent = Intent(this, ParticipantsActivity::class.java)
            intent.putExtra("MISSION_ID", missionId)
            startActivity(intent)
        }
        binding.btnViewAllRequests.setOnClickListener {
            val intent = Intent(this, AllRequestsActivity::class.java)
            intent.putExtra("MISSION_ID", missionId)
            startActivity(intent)
        }

    }

    private fun setEditingMode(isEditing: Boolean) {
        binding.editTextMissionName.isEnabled = isEditing
        binding.editTextMissionDescription.isEnabled = isEditing
        binding.editTextMissionLieu.isEnabled = isEditing
        binding.editTextMissionDateDebut.isEnabled = isEditing
        binding.editTextMissionDateFin.isEnabled = isEditing
        binding.editTextMissionStatut.isEnabled = isEditing
        binding.editTextMissionNbrMedecin.isEnabled = isEditing
        binding.editTextMissionNbrInfirmier.isEnabled = isEditing
        binding.editTextMissionNbrVolontaire.isEnabled = isEditing
    }

    private fun saveMissionChanges(missionRef: DocumentReference) {
        val updatedMission = mapOf(
            "titre" to binding.editTextMissionName.text.toString(),
            "description" to binding.editTextMissionDescription.text.toString(),
            "lieu" to binding.editTextMissionLieu.text.toString(),
            "dateDebut" to binding.editTextMissionDateDebut.text.toString().toLongOrNull(),
            "dateFin" to binding.editTextMissionDateFin.text.toString().toLongOrNull(),
            "statut" to binding.editTextMissionStatut.text.toString(),
            "nbrMedecin" to binding.editTextMissionNbrMedecin.text.toString().toIntOrNull(),
            "nbrInfirmier" to binding.editTextMissionNbrInfirmier.text.toString().toIntOrNull(),
            "nbrVolontaire" to binding.editTextMissionNbrVolontaire.text.toString().toIntOrNull()
        )

        missionRef.update(updatedMission)
            .addOnSuccessListener {
                Toast.makeText(this, "Mission mise à jour !", Toast.LENGTH_SHORT).show()
                Log.d("MissionDetail", "Mission mise à jour avec succès")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                Log.e("MissionDetail", "Erreur update", e)
            }
    }


    private fun sendEmail(to: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            startActivity(Intent.createChooser(intent, "Envoyer email..."))
        } catch (e: Exception) {
            Toast.makeText(this, "Aucun client email trouvé", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmailToConfirmedParticipants(missionId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("demandesParticipation")
            .whereEqualTo("missionId", missionId)
            .whereEqualTo("statut", DemandeStatus.ACCEPTEE.name)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    val userId = doc.getString("userId")
                    if (!userId.isNullOrEmpty()) {
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { userDoc ->
                                userDoc.getString("email")?.let {
                                    sendEmail(
                                        it,
                                        "Annulation de mission",
                                        "La mission a été annulée. Merci de votre compréhension."
                                    )
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("MissionDetail", "Erreur récupération email utilisateur", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MissionDetail", "Erreur récupération participants confirmés", e)
            }
    }
}

package com.example.healthproject.ui.coordinateur

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.model.Mission
import com.example.healthproject.databinding.ActivityMissionDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class MissionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMissionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val missionId = intent.getStringExtra("MISSION_ID")
        if (missionId != null) {
            FirebaseFirestore.getInstance().collection("missions")
                .document(missionId)
                .get()
                .addOnSuccessListener { doc ->
                    val mission = doc.toObject(Mission::class.java)
                    mission?.let {
                        binding.textViewMissionName.text = it.titre
                        binding.textViewMissionDescription.text = it.description
                        binding.textViewMissionLieu.text = "📍 ${it.lieu}"
                        binding.textViewMissionDateDebut.text = "Début : ${it.dateDebut}"
                        binding.textViewMissionDateFin.text = "Fin : ${it.dateFin}"
                        binding.textViewMissionStatut.text = "Statut : ${it.statut}"
                        binding.textViewMissionNbrMedecin.text = "Médecins : ${it.nbrMedecin}"
                        binding.textViewMissionNbrInfirmier.text = "Infirmiers : ${it.nbrInfirmier}"
                        binding.textViewMissionNbrVolontaire.text = "Volontaires : ${it.nbrVolontaire}"
                    }
                }
        }

        // Ajout du listener pour ouvrir ParticipantsActivity
        binding.btnViewParticipants.setOnClickListener {
            missionId?.let {
                val intent = Intent(this, ParticipantsActivity::class.java)
                intent.putExtra("MISSION_ID", it)
                startActivity(intent)
            }
        }
    }
}

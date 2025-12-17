package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.model.Mission
import com.example.healthproject.databinding.ActivityCreateMissionBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreateMissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateMissionBinding
    private val db = FirebaseFirestore.getInstance() // <-- Instance Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateMission.setOnClickListener {
            val titre = binding.editTextMissionName.text.toString()
            val description = binding.editTextDescription.text.toString()
            val lieu = binding.editTextLieu.text.toString()
            val dateDebut = binding.editTextDateDebut.text.toString().toLongOrNull() ?: 0L
            val dateFin = binding.editTextDateFin.text.toString().toLongOrNull() ?: 0L
            val nbrMedecin = binding.editTextNbrMedecin.text.toString().toIntOrNull() ?: 0
            val nbrInfirmier = binding.editTextNbrInfirmier.text.toString().toIntOrNull() ?: 0
            val nbrVolontaire = binding.editTextNbrVolontaire.text.toString().toIntOrNull() ?: 0

            if (titre.isNotEmpty()) {
                val mission = Mission(
                    titre = titre,
                    description = description,
                    lieu = lieu,
                    dateDebut = dateDebut,
                    dateFin = dateFin,
                    nbrMedecin = nbrMedecin,
                    nbrInfirmier = nbrInfirmier,
                    nbrVolontaire = nbrVolontaire
                )

                // 🔹 Ajouter la mission dans Firestore
                db.collection("missions")
                    .add(mission)
                    .addOnSuccessListener { docRef ->
                        Toast.makeText(
                            this,
                            "Mission créée avec ID: ${docRef.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // Retour à la liste des missions
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Toast.makeText(this, "Veuillez entrer un nom de mission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

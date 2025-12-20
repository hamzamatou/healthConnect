package com.example.healthproject.ui.coordinateur

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*

class MissionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMissionDetailBinding
    private var isEditing = false
    private val db = FirebaseFirestore.getInstance()
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Stockage des timestamps pour la base de données
    private var selectedDateDebut: Long? = null
    private var selectedDateFin: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        val missionId = intent.getStringExtra("MISSION_ID")
        if (missionId == null) {
            Toast.makeText(this, "ID Mission manquant", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val missionRef = db.collection("missions").document(missionId)

        loadMissionDetails(missionRef)
        setEditingMode(false)

        // Gestion des Pickers (uniquement en mode édition)
        binding.editTextMissionDateDebut.setOnClickListener { if (isEditing) showDateTimePicker(true) }
        binding.editTextMissionDateFin.setOnClickListener { if (isEditing) showDateTimePicker(false) }

        // FAB Modifier / Enregistrer
        binding.fabEditMission.setOnClickListener {
            if (isEditing) {
                saveMissionChanges(missionRef)
            } else {
                isEditing = true
                setEditingMode(true)
                binding.fabEditMission.setImageResource(android.R.drawable.ic_menu_save)
                Toast.makeText(this, "Mode édition activé", Toast.LENGTH_SHORT).show()
            }
        }

        // FAB Annuler mission
        binding.fabCancelMission.setOnClickListener { cancelMission(missionRef, missionId) }

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
                selectedDateDebut = it.dateDebut
                selectedDateFin = it.dateFin

                binding.apply {
                    editTextMissionName.setText(it.titre)
                    editTextMissionDescription.setText(it.description)
                    editTextMissionLieu.setText(it.lieu)
                    editTextMissionDateDebut.setText(sdf.format(Date(it.dateDebut)))
                    editTextMissionDateFin.setText(sdf.format(Date(it.dateFin)))
                    editTextMissionStatut.setText(it.statut.name)
                    editTextMissionNbrMedecin.setText(it.nbrMedecin.toString())
                    editTextMissionNbrInfirmier.setText(it.nbrInfirmier.toString())
                    editTextMissionNbrVolontaire.setText(it.nbrVolontaire.toString())

                    if (it.statut == MissionStatus.ANNULE) {
                        fabCancelMission.isEnabled = false
                        fabCancelMission.alpha = 0.5f
                    }
                }
            }
        }
    }

    private fun showDateTimePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            val selCal = Calendar.getInstance().apply { set(y, m, d) }
            TimePickerDialog(this, { _, h, min ->
                selCal.set(Calendar.HOUR_OF_DAY, h)
                selCal.set(Calendar.MINUTE, min)
                val time = selCal.timeInMillis

                if (isStartDate) {
                    selectedDateDebut = time
                    binding.editTextMissionDateDebut.setText(sdf.format(Date(time)))
                } else {
                    if (selectedDateDebut != null && time <= selectedDateDebut!!) {
                        Toast.makeText(this, "La fin doit être après le début", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedDateFin = time
                        binding.editTextMissionDateFin.setText(sdf.format(Date(time)))
                    }
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun setEditingMode(enabled: Boolean) {
        binding.apply {
            editTextMissionName.isEnabled = enabled
            editTextMissionDescription.isEnabled = enabled
            editTextMissionLieu.isEnabled = enabled
            editTextMissionDateDebut.isEnabled = enabled
            editTextMissionDateFin.isEnabled = enabled
            editTextMissionNbrMedecin.isEnabled = enabled
            editTextMissionNbrInfirmier.isEnabled = enabled
            editTextMissionNbrVolontaire.isEnabled = enabled
        }
    }

    private fun saveMissionChanges(missionRef: DocumentReference) {
        val titre = binding.editTextMissionName.text.toString().trim()
        val desc = binding.editTextMissionDescription.text.toString().trim()
        val lieu = binding.editTextMissionLieu.text.toString().trim()
        val nMed = binding.editTextMissionNbrMedecin.text.toString().toIntOrNull() ?: 0
        val nInf = binding.editTextMissionNbrInfirmier.text.toString().toIntOrNull() ?: 0
        val nVol = binding.editTextMissionNbrVolontaire.text.toString().toIntOrNull() ?: 0

        if (titre.isEmpty() || desc.isEmpty() || lieu.isEmpty() || selectedDateDebut == null || selectedDateFin == null) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf(
            "titre" to titre,
            "description" to desc,
            "lieu" to lieu,
            "dateDebut" to selectedDateDebut!!,
            "dateFin" to selectedDateFin!!,
            "nbrMedecin" to nMed,
            "nbrInfirmier" to nInf,
            "nbrVolontaire" to nVol
        )

        missionRef.update(updates).addOnSuccessListener {
            isEditing = false
            setEditingMode(false)
            binding.fabEditMission.setImageResource(com.example.healthproject.R.drawable.ic_edit1)
            Toast.makeText(this, "Mission mise à jour !", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelMission(missionRef: DocumentReference, missionId: String) {
        missionRef.update("statut", MissionStatus.ANNULE.name).addOnSuccessListener {
            Toast.makeText(this, "Mission annulée !", Toast.LENGTH_SHORT).show()
            binding.editTextMissionStatut.setText(MissionStatus.ANNULE.name)
            sendEmailToConfirmedParticipants(missionId)
        }
    }

    private fun sendEmailToConfirmedParticipants(missionId: String) {
        // ... (Logique Brevo inchangée)
        val retrofit = Retrofit.Builder().baseUrl("https://api.brevo.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(BrevoApi::class.java)
        val apiKey = "votre_cle"

        db.collection("demandesParticipation")
            .whereEqualTo("missionId", missionId)
            .whereEqualTo("statut", DemandeStatus.ACCEPTEE.name).get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    val uId = doc.getString("userId") ?: continue
                    db.collection("users").document(uId).get().addOnSuccessListener { uDoc ->
                        val email = uDoc.getString("email") ?: return@addOnSuccessListener
                        val body = EmailBody(
                            sender = Sender("HealthApp", "nourkalay21@gmail.com"),
                            to = listOf(Recipient(email)),
                            subject = "Annulation de mission",
                            textContent = "La mission a été annulée."
                        )
                        service.sendEmail(body, apiKey).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.isSuccessful) Log.d("Email", "Envoyé à $email")
                            }
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
                        })
                    }
                }
            }
    }
}
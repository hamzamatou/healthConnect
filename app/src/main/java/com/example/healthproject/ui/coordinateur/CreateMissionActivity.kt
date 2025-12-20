package com.example.healthproject.ui.coordinateur

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.data.model.AffectationMateriel
import com.example.healthproject.data.model.Materiel
import com.example.healthproject.data.model.MaterielSelection
import com.example.healthproject.data.model.Mission
import com.example.healthproject.databinding.ActivityCreateMissionBinding
import com.example.healthproject.ui.coordinateur.adapter.MaterielSelectionAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.*

class CreateMissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateMissionBinding
    private val db = FirebaseFirestore.getInstance()

    private val materielSelections = mutableListOf<MaterielSelection>()
    private val materiels = mutableListOf<Materiel>()
    private lateinit var materielAdapter: MaterielSelectionAdapter

    private var selectedImageBase64: String? = null
    private var selectedDateDebut: Long? = null
    private var selectedDateFin: Long? = null

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView matériels
        materielAdapter = MaterielSelectionAdapter(
            materiels,
            materielSelections
        ) { position ->
            materielSelections.removeAt(position)
            materielAdapter.notifyItemRemoved(position)
            materielAdapter.notifyItemRangeChanged(position, materielSelections.size)
        }

        binding.rvMateriels.apply {
            layoutManager = LinearLayoutManager(this@CreateMissionActivity)
            adapter = materielAdapter
        }

        // Charger matériels depuis Firestore
        db.collection("materiel").get().addOnSuccessListener { snapshot ->
            materiels.clear()
            materiels.addAll(snapshot.toObjects(Materiel::class.java))
            materielAdapter.notifyDataSetChanged()
        }

        // Ajouter une ligne
        binding.btnAddMateriel.setOnClickListener {
            materielSelections.add(MaterielSelection())
            materielAdapter.notifyItemInserted(materielSelections.size - 1)
        }

        // Sélection image
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Date & heure picker
        binding.editTextDateDebut.setOnClickListener { showDateTimePicker(isStartDate = true) }
        binding.editTextDateFin.setOnClickListener { showDateTimePicker(isStartDate = false) }

        // Création mission
        binding.btnCreateMission.setOnClickListener { createMission() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data ?: return
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImageBase64 = encodeToBase64(bitmap)
            binding.imageViewPreview.setImageBitmap(bitmap)
            Toast.makeText(this, "Image sélectionnée", Toast.LENGTH_SHORT).show()
        }
    }

    private fun encodeToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }

    // 🔹 Date + Heure Picker
    private fun showDateTimePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()

        // Définir la contrainte de base (Maintenant + 24h)
        val minAllowedDate = Calendar.getInstance()
        minAllowedDate.add(Calendar.HOUR_OF_DAY, 24)

        val datePicker = DatePickerDialog(
            this,
            { _, y, m, d ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(y, m, d)

                val timePicker = TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hour)
                        selectedCalendar.set(Calendar.MINUTE, minute)
                        selectedCalendar.set(Calendar.SECOND, 0)
                        selectedCalendar.set(Calendar.MILLISECOND, 0)

                        val timestamp = selectedCalendar.timeInMillis

                        if (isStartDate) {
                            selectedDateDebut = timestamp
                            binding.editTextDateDebut.setText(String.format("%02d/%02d/%04d %02d:%02d", d, m + 1, y, hour, minute))

                            // Reset la date de fin si elle devient invalide par rapport au nouveau début
                            if (selectedDateFin != null && selectedDateFin!! <= selectedDateDebut!!) {
                                selectedDateFin = null
                                binding.editTextDateFin.setText("")
                            }
                        } else {
                            // 🔹 Vérification supplémentaire pour l'heure si c'est la date de fin
                            if (selectedDateDebut != null && timestamp <= selectedDateDebut!!) {
                                Toast.makeText(this, "L'heure de fin doit être après l'heure de début", Toast.LENGTH_LONG).show()
                            } else {
                                selectedDateFin = timestamp
                                binding.editTextDateFin.setText(String.format("%02d/%02d/%04d %02d:%02d", d, m + 1, y, hour, minute))
                            }
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // --- LOGIQUE DES CONTRAINTES ---
        if (isStartDate) {
            // La date de début ne peut pas être avant "Maintenant + 24h"
            datePicker.datePicker.minDate = minAllowedDate.timeInMillis
        } else {
            // La date de fin ne peut pas être avant la date de début (ou minAllowedDate si début non saisi)
            if (selectedDateDebut != null) {
                datePicker.datePicker.minDate = selectedDateDebut!!
            } else {
                datePicker.datePicker.minDate = minAllowedDate.timeInMillis
            }
        }

        datePicker.show()
    }

    private fun createMission() {
        // 1. Récupération des valeurs
        val titre = binding.editTextMissionName.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val lieu = binding.editTextLieu.text.toString().trim()
        val nbrMed = binding.editTextNbrMedecin.text.toString().trim()
        val nbrInf = binding.editTextNbrInfirmier.text.toString().trim()
        val nbrVol = binding.editTextNbrVolontaire.text.toString().trim()

        // 2. Vérification des champs textuels et numériques
        if (titre.isEmpty() || description.isEmpty() || lieu.isEmpty() ||
            nbrMed.isEmpty() || nbrInf.isEmpty() || nbrVol.isEmpty()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Vérification de l'image
        if (selectedImageBase64 == null) {
            Toast.makeText(this, "Veuillez sélectionner une image pour la mission", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Vérification des dates (déjà gérée par vos sélecteurs, mais sécurité additionnelle)
        if (selectedDateDebut == null || selectedDateFin == null) {
            Toast.makeText(this, "Veuillez sélectionner les dates de début et de fin", Toast.LENGTH_SHORT).show()
            return
        }

        // 5. Vérification du matériel (Optionnel : au moins un matériel ?)
        if (materielSelections.isEmpty()) {
            Toast.makeText(this, "Veuillez ajouter au moins un matériel", Toast.LENGTH_SHORT).show()
            return
        }

        // Si tout est OK, on crée l'objet Mission
        val mission = Mission(
            titre = titre,
            description = description,
            lieu = lieu,
            dateDebut = selectedDateDebut!!,
            dateFin = selectedDateFin!!,
            nbrMedecin = nbrMed.toInt(),
            nbrInfirmier = nbrInf.toInt(),
            nbrVolontaire = nbrVol.toInt(),
            imageBase64 = selectedImageBase64
        )

        db.collection("missions").add(mission)
            .addOnSuccessListener { docRef ->
                val missionId = docRef.id
                var hasError = false

                materielSelections.forEach { sel ->
                    if (sel.materielId == null || sel.quantite <= 0) return@forEach

                    db.collection("materiel").document(sel.materielId!!).get()
                        .addOnSuccessListener { snapshot ->
                            val materiel = snapshot.toObject(Materiel::class.java)
                            if (materiel == null) {
                                Toast.makeText(this, "Matériel introuvable", Toast.LENGTH_SHORT).show()
                                hasError = true
                                return@addOnSuccessListener
                            }

                            if (sel.quantite > materiel.quantiteInitiale) {
                                Toast.makeText(
                                    this,
                                    "Quantité demandée (${sel.quantite}) pour ${materiel.nom} dépasse le stock (${materiel.quantiteInitiale})",
                                    Toast.LENGTH_LONG
                                ).show()
                                hasError = true
                                return@addOnSuccessListener
                            }

                            val affectation = AffectationMateriel(
                                missionId = missionId,
                                materielId = sel.materielId!!,
                                quantiteAffectee = sel.quantite,
                                etatAvant = "Stock: ${materiel.quantiteInitiale}"
                            )
                            db.collection("affectationsMateriel").add(affectation)
                            db.collection("materiel").document(sel.materielId!!)
                                .update("quantiteInitiale", materiel.quantiteInitiale - sel.quantite)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erreur récupération stock", Toast.LENGTH_SHORT).show()
                            hasError = true
                        }
                }

                if (!hasError) {
                    Toast.makeText(this, "Mission créée avec succès", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}

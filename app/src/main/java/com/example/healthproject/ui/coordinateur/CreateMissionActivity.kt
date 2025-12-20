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
import com.example.healthproject.data.model.*
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

        setupMaterielRecycler()
        loadMateriels()

        binding.btnAddMateriel.setOnClickListener {
            materielSelections.add(MaterielSelection())
            materielAdapter.notifyItemInserted(materielSelections.size - 1)
        }

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.editTextDateDebut.setOnClickListener { showDateTimePicker(true) }
        binding.editTextDateFin.setOnClickListener { showDateTimePicker(false) }

        binding.btnCreateMission.setOnClickListener { createMission() }
    }

    private fun setupMaterielRecycler() {
        materielAdapter = MaterielSelectionAdapter(
            materiels,
            materielSelections
        ) { position ->
            if (position in materielSelections.indices) {
                materielSelections.removeAt(position)
                materielAdapter.notifyItemRemoved(position)
            }
        }

        binding.rvMateriels.apply {
            layoutManager = LinearLayoutManager(this@CreateMissionActivity)
            adapter = materielAdapter
        }
    }

    private fun loadMateriels() {
        db.collection("materiel").get().addOnSuccessListener { snapshot ->
            materiels.clear()
            materiels.addAll(snapshot.toObjects(Materiel::class.java))
            materielAdapter.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            selectedImageBase64 = encodeToBase64(bitmap)
            binding.imageViewPreview.setImageBitmap(bitmap)
            Toast.makeText(this, "Image sélectionnée", Toast.LENGTH_SHORT).show()
        }
    }

    private fun encodeToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos) // compression forte
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }

    private fun showDateTimePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, y, m, d ->
                val selected = Calendar.getInstance().apply {
                    set(y, m, d)
                }

                TimePickerDialog(
                    this,
                    { _, h, min ->
                        selected.set(Calendar.HOUR_OF_DAY, h)
                        selected.set(Calendar.MINUTE, min)
                        val timestamp = selected.timeInMillis

                        if (isStartDate) {
                            selectedDateDebut = timestamp
                            binding.editTextDateDebut.setText(
                                String.format("%02d/%02d/%04d %02d:%02d", d, m + 1, y, h, min)
                            )
                            selectedDateFin = null
                            binding.editTextDateFin.setText("")
                        } else {
                            if (selectedDateDebut != null && timestamp <= selectedDateDebut!!) {
                                Toast.makeText(this, "La date de fin doit être après le début", Toast.LENGTH_SHORT).show()
                            } else {
                                selectedDateFin = timestamp
                                binding.editTextDateFin.setText(
                                    String.format("%02d/%02d/%04d %02d:%02d", d, m + 1, y, h, min)
                                )
                            }
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun createMission() {
        val titre = binding.editTextMissionName.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val lieu = binding.editTextLieu.text.toString().trim()

        val nbrMed = binding.editTextNbrMedecin.text.toString().toIntOrNull()
        val nbrInf = binding.editTextNbrInfirmier.text.toString().toIntOrNull()
        val nbrVol = binding.editTextNbrVolontaire.text.toString().toIntOrNull()

        if (titre.isEmpty() || description.isEmpty() || lieu.isEmpty() ||
            nbrMed == null || nbrInf == null || nbrVol == null) {
            Toast.makeText(this, "Veuillez remplir tous les champs correctement", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageBase64 == null || selectedDateDebut == null || selectedDateFin == null) {
            Toast.makeText(this, "Image et dates obligatoires", Toast.LENGTH_SHORT).show()
            return
        }

        if (materielSelections.isEmpty()) {
            Toast.makeText(this, "Ajoutez au moins un matériel", Toast.LENGTH_SHORT).show()
            return
        }

        val mission = Mission(
            titre = titre,
            description = description,
            lieu = lieu,
            dateDebut = selectedDateDebut!!,
            dateFin = selectedDateFin!!,
            nbrMedecin = nbrMed,
            nbrInfirmier = nbrInf,
            nbrVolontaire = nbrVol,
            imageBase64 = selectedImageBase64
        )

        db.collection("missions").add(mission)
            .addOnSuccessListener {
                Toast.makeText(this, "Mission créée avec succès", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }
}

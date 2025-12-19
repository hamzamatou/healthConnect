package com.example.healthproject.ui.coordinateur

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

class CreateMissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateMissionBinding
    private val db = FirebaseFirestore.getInstance()

    private val materielSelections = mutableListOf<MaterielSelection>()
    private val materiels = mutableListOf<Materiel>() // à remplir depuis Firestore
    private lateinit var materielAdapter: MaterielSelectionAdapter

    private var selectedImageBase64: String? = null

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 RecyclerView
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

        // 🔹 Charger la liste des matériels depuis Firestore
        db.collection("materiel").get().addOnSuccessListener { snapshot ->
            materiels.clear()
            materiels.addAll(snapshot.toObjects(Materiel::class.java))
            materielAdapter.notifyDataSetChanged()
        }

        // 🔹 Ajouter une ligne
        binding.btnAddMateriel.setOnClickListener {
            materielSelections.add(MaterielSelection())
            materielAdapter.notifyItemInserted(materielSelections.size - 1)
        }

        // 🔹 Sélection image
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // 🔹 Création mission
        binding.btnCreateMission.setOnClickListener {
            createMission()
        }
    }

    // 🔹 Gestion résultat sélection image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data ?: return
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImageBase64 = encodeToBase64(bitmap)
            Toast.makeText(this, "Image sélectionnée", Toast.LENGTH_SHORT).show()
        }
    }

    private fun encodeToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // qualité 80%
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun createMission() {
        val titre = binding.editTextMissionName.text.toString().trim()
        if (titre.isEmpty()) {
            Toast.makeText(this, "Nom de mission obligatoire", Toast.LENGTH_SHORT).show()
            return
        }

        val mission = Mission(
            titre = titre,
            description = binding.editTextDescription.text.toString(),
            lieu = binding.editTextLieu.text.toString(),
            dateDebut = binding.editTextDateDebut.text.toString().toLongOrNull() ?: 0L,
            dateFin = binding.editTextDateFin.text.toString().toLongOrNull() ?: 0L,
            nbrMedecin = binding.editTextNbrMedecin.text.toString().toIntOrNull() ?: 0,
            nbrInfirmier = binding.editTextNbrInfirmier.text.toString().toIntOrNull() ?: 0,
            nbrVolontaire = binding.editTextNbrVolontaire.text.toString().toIntOrNull() ?: 0,
            imageBase64 = selectedImageBase64 // <-- ajout de l'image
        )

        db.collection("missions").add(mission)
            .addOnSuccessListener { docRef ->
                val missionId = docRef.id
                var hasError = false

                materielSelections.forEach { sel ->
                    if (sel.materielId == null || sel.quantite <= 0) return@forEach

                    // 🔹 Récupérer la quantité réelle depuis Firestore
                    db.collection("materiel").document(sel.materielId!!).get()
                        .addOnSuccessListener { snapshot ->
                            val materiel = snapshot.toObject(Materiel::class.java)
                            if (materiel == null) {
                                Toast.makeText(this, "Matériel introuvable", Toast.LENGTH_SHORT).show()
                                hasError = true
                                return@addOnSuccessListener
                            }

                            // 🔹 Vérification stock
                            if (sel.quantite > materiel.quantiteInitiale) {
                                Toast.makeText(
                                    this,
                                    "Quantité demandée (${sel.quantite}) pour ${materiel.nom} dépasse le stock (${materiel.quantiteInitiale})",
                                    Toast.LENGTH_LONG
                                ).show()
                                hasError = true
                                return@addOnSuccessListener
                            }

                            // 🔹 Créer affectation
                            val affectation = AffectationMateriel(
                                missionId = missionId,
                                materielId = sel.materielId!!,
                                quantiteAffectee = sel.quantite,
                                etatAvant = "Stock: ${materiel.quantiteInitiale}"
                            )
                            db.collection("affectationsMateriel").add(affectation)

                            // 🔹 Mettre à jour le stock dans Firestore
                            db.collection("materiel").document(sel.materielId!!)
                                .update("quantiteInitiale", materiel.quantiteInitiale - sel.quantite)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erreur récupération stock", Toast.LENGTH_SHORT).show()
                            hasError = true
                        }
                }

                if (!hasError) {
                    Toast.makeText(this, "Mission créée avec matériels et image", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}

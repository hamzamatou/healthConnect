package com.example.healthproject.ui.coordinateur

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.healthproject.data.model.Mission
import com.example.healthproject.databinding.ActivityCoordinateurMissionsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.healthproject.ui.coordinateur.adapter.MissionAdapter

class CoordinateurMissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoordinateurMissionsBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: MissionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinateurMissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView en grille (2 cards par ligne)
        binding.recyclerViewMissions.layoutManager = GridLayoutManager(this, 2)

        // Adapter avec clic sur une mission → ouvrir les détails
        // Adapter avec clic géré dans le ViewHolder
        adapter = MissionAdapter()
        binding.recyclerViewMissions.adapter = adapter

        binding.recyclerViewMissions.adapter = adapter

        // Charger les missions depuis Firestore
        loadMissions()

        // FAB pour créer une mission
        binding.fabAddMission.setOnClickListener {
            startActivity(Intent(this, CreateMissionActivity::class.java))
        }
    }

    private fun loadMissions() {
        db.collection("missions")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { doc ->
                    doc.toObject(Mission::class.java).copy(id = doc.id)
                }
                adapter.setMissions(list)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

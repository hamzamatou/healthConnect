package com.example.healthproject.ui.coordinateur

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.healthproject.data.model.Mission
import com.example.healthproject.data.model.UserType
import com.example.healthproject.databinding.ActivityCoordinateurMissionsBinding
import com.example.healthproject.ui.coordinateur.adapter.MissionAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import androidx.core.widget.addTextChangedListener
import com.example.healthproject.R
import com.example.healthproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class CoordinateurMissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoordinateurMissionsBinding
    private lateinit var adapter: MissionAdapter
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinateurMissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Layout manager pour la grille
        binding.recyclerViewMissions.layoutManager = GridLayoutManager(this, 2)

        // 🔹 Adapter avec rôle utilisateur
        adapter = MissionAdapter(UserType.COORDINATEUR)
        binding.recyclerViewMissions.adapter = adapter

        // 🔹 Listener temps réel pour missions
        listenerRegistration = db.collection("missions")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this, "Erreur: ${exception.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val missions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Mission::class.java)?.copy(id = doc.id)
                } ?: listOf()

                adapter.setMissions(missions)
            }

        // 🔹 FAB pour créer une mission
        binding.fabAddMission.setOnClickListener {
            startActivity(Intent(this, CreateMissionActivity::class.java))
        }


        binding.searchMission.addTextChangedListener { editable ->
            adapter.filter(editable.toString())
        }

        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, CoordinateurMissionsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfilCoordonnateurActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove() // Supprimer l'écouteur pour éviter les fuites mémoire
    }
}

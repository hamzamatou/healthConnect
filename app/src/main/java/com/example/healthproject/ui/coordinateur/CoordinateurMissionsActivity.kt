package com.example.healthproject.ui.coordinateur

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.healthproject.data.model.Mission
import com.example.healthproject.data.model.MissionStatus
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

    // Liste complète pour filtrer localement sans recharger Firebase inutilement
    private var allMissionsList: List<Mission> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinateurMissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupStatusSpinner()
        loadMissionsRealtime()

        binding.fabAddMission.setOnClickListener {
            startActivity(Intent(this, CreateMissionActivity::class.java))
        }

        binding.searchMission.addTextChangedListener { editable ->
            adapter.filter(editable.toString())
        }

        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewMissions.layoutManager = GridLayoutManager(this, 2)
        adapter = MissionAdapter(UserType.COORDINATEUR)
        binding.recyclerViewMissions.adapter = adapter
    }

    private fun setupStatusSpinner() {
        // 1. On récupère les statuts et on enlève "EN_COURS"
        val options = mutableListOf("Tous")

        val filteredStatuses = MissionStatus.values()
            .filter { it != MissionStatus.EN_COURS } // On retire EN_COURS ici
            .map { it.name }

        options.addAll(filteredStatuses)

        // 2. Configuration de l'adapter du Spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatusFilter.adapter = spinnerAdapter

        // 3. Sélection par défaut sur "OUVERTE"
        val defaultPosition = options.indexOf(MissionStatus.OUVERTE.name)
        if (defaultPosition != -1) {
            binding.spinnerStatusFilter.setSelection(defaultPosition)
        }

        // 4. Listener pour filtrer la liste quand on change de statut
        binding.spinnerStatusFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadMissionsRealtime() {
        listenerRegistration = db.collection("missions")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) return@addSnapshotListener

                allMissionsList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Mission::class.java)?.copy(id = doc.id)
                } ?: listOf()

                applyFilters() // Appliquer le filtre dès que les données arrivent
            }
    }

    private fun applyFilters() {
        val selectedStatus = binding.spinnerStatusFilter.selectedItem.toString()
        val searchText = binding.searchMission.text.toString()

        var filteredList = allMissionsList

        // 1. Filtre par Statut
        if (selectedStatus != "Tous") {
            filteredList = filteredList.filter { it.statut.name == selectedStatus }
        }

        // 2. Filtre par Recherche (optionnel si votre adapter le gère déjà)
        if (searchText.isNotEmpty()) {
            filteredList = filteredList.filter { it.titre.contains(searchText, ignoreCase = true) }
        }

        adapter.setMissions(filteredList)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
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

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}
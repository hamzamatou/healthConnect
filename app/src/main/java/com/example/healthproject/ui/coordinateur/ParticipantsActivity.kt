package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.RoleMission
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.databinding.ActivityParticipantsBinding
import com.example.healthproject.ui.coordinateur.adapter.ParticipantsAdapter

class ParticipantsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParticipantsBinding
    private val repository = DemandeParticipationRepository()
    private val adapter = ParticipantsAdapter()
    private var allParticipants: List<DemandeParticipation> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Bouton Retour
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. Configuration RecyclerView
        binding.recyclerViewParticipants.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewParticipants.adapter = adapter

        // 3. Récupération Mission ID
        val missionId = intent.getStringExtra("MISSION_ID")
        if (missionId.isNullOrEmpty()) {
            Toast.makeText(this, "Mission ID introuvable", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 4. Charger les données
        loadAcceptedParticipants(missionId)

        // 5. Configuration Spinner (Filtre)
        setupRoleSpinner()
    }

    private fun loadAcceptedParticipants(missionId: String) {
        repository.getDemandesByMissionAndStatus(missionId, DemandeStatus.ACCEPTEE) { list ->
            allParticipants = list
            adapter.setParticipants(list)
            if (list.isEmpty()) {
                Toast.makeText(this, "Aucun participant confirmé", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRoleSpinner() {
        val roles = RoleMission.values().map { it.name }.toMutableList()
        roles.add(0, "Tous les participants") // Nom plus pro

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRoleFilter.adapter = spinnerAdapter

        binding.spinnerRoleFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRole = roles[position]
                val filtered = if (position == 0) {
                    allParticipants
                } else {
                    allParticipants.filter { it.roleMission.name == selectedRole }
                }
                adapter.setParticipants(filtered)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
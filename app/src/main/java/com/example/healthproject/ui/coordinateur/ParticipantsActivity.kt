package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
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

        binding.recyclerViewParticipants.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewParticipants.adapter = adapter

        val missionId = intent.getStringExtra("MISSION_ID") ?: return

        // Charger toutes les demandes acceptées pour la mission
        repository.getDemandesByMissionAndStatus(missionId, DemandeStatus.ACCEPTEE) { list ->
            allParticipants = list
            adapter.setParticipants(list)
        }

        // Spinner pour filtrer par rôle
        val spinner: Spinner = binding.spinnerRoleFilter
        val roles = RoleMission.values().map { it.name }.toMutableList()
        roles.add(0, "Tous") // option "Tous"
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedRole = roles[position]
                val filtered = if (selectedRole == "Tous") allParticipants
                else allParticipants.filter { it.roleMission.name == selectedRole }
                adapter.setParticipants(filtered)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                adapter.setParticipants(allParticipants)
            }
        }

    }
}

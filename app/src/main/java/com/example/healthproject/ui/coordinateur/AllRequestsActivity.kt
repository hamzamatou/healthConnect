package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.databinding.ActivityAllRequestsBinding
import com.example.healthproject.ui.coordinateur.adapter.RequestsAdapter

class AllRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllRequestsBinding
    private val repository = DemandeParticipationRepository()
    private lateinit var adapter: RequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Gestion du bouton retour
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. Configuration de la RecyclerView
        setupRecyclerView()

        // 3. Chargement des données
        loadRequests()
    }

    private fun setupRecyclerView() {
        adapter = RequestsAdapter(onStatusChanged = {
            // Recharge la liste après une acceptation ou un refus
            loadRequests()
        })
        binding.recyclerViewRequests.apply {
            layoutManager = LinearLayoutManager(this@AllRequestsActivity)
            adapter = this@AllRequestsActivity.adapter
        }
    }

    private fun loadRequests() {
        val missionId = intent.getStringExtra("MISSION_ID") ?: return

        // 🔹 Ne récupérer que les demandes EN_ATTENTE pour cette mission
        repository.getDemandesByMissionAndStatus(missionId, DemandeStatus.EN_ATTENTE) { list ->
            adapter.setRequests(list)
        }
    }
}
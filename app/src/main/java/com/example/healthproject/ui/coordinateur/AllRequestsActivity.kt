package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.data.model.DemandeParticipation
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

        adapter = RequestsAdapter(onStatusChanged = { loadRequests() })
        binding.recyclerViewRequests.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRequests.adapter = adapter

        loadRequests()
    }

    private fun loadRequests() {
        val missionId = intent.getStringExtra("MISSION_ID") ?: return

        // 🔹 Ne récupérer que les demandes EN_ATTENTE
        repository.getDemandesByMissionAndStatus(missionId, DemandeStatus.EN_ATTENTE) { list ->
            adapter.setRequests(list)
        }
    }
}



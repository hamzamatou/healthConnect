package com.example.healthproject.ui.superviseur.presence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.data.repository.AffectationMaterielRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.viewmodel.SuperviseurViewModel
import com.example.healthproject.viewmodel.factory.SuperviseurViewModelFactory

class PresenceFragment : Fragment() {

    private val args: PresenceFragmentArgs by navArgs()
    private lateinit var viewModel: SuperviseurViewModel
    private lateinit var adapter: PresenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val affectationRepo = AffectationMaterielRepository()
        val participationRepo = DemandeParticipationRepository()
        val missionRepo = MissionRepository()

        val factory = SuperviseurViewModelFactory(
            affectationRepo,
            participationRepo,
            missionRepo
        )

        viewModel = ViewModelProvider(this, factory)[SuperviseurViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_presence, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerPresence)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = PresenceAdapter(viewModel)
        recycler.adapter = adapter

        // Observer la liste des participations
        viewModel.participations.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // Charger les participants de la mission
        viewModel.loadParticipants(args.missionId)

        return view
    }
}

package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MessageRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.databinding.FragmentMesDemandesBinding
import com.example.healthproject.viewmodel.ParticipantMissionViewModel
import com.example.healthproject.viewmodel.ParticipantMissionViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MesDemandesFragment : Fragment() {

    private var _binding: FragmentMesDemandesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ParticipantMissionViewModel by viewModels {
        ParticipantMissionViewModelFactory(
            MissionRepository(),
            DemandeParticipationRepository(),
            MessageRepository()
        )
    }

    private lateinit var adapter: MesDemandesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMesDemandesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewDemandes.layoutManager = LinearLayoutManager(requireContext())

        setupStatusFilter()

        // 1. Charger les missions en premier
        viewModel.loadMissions()

        // 2. Observer les demandes : C'est ici qu'on initialise l'adapter proprement
        viewModel.mesDemandes.observe(viewLifecycleOwner) { demandes ->
            // On crée l'adapter SEULEMENT quand les données (missionsMap) sont prêtes dans le ViewModel
            adapter = MesDemandesAdapter(viewModel.missionsMap)
            binding.recyclerViewDemandes.adapter = adapter

            // 3. Configurer le clic de navigation
            adapter.onDetailClickListener = { mission ->
                val missionId = mission.id ?: ""
                if (missionId.isNotEmpty()) {
                    val action = MesDemandesFragmentDirections
                        .actionMesDemandesFragmentToDescriptionMissionFragment(missionId)
                    findNavController().navigate(action)
                }
            }

            adapter.submitListWithFilter(demandes, getSelectedStatus())
            binding.tvEmpty.visibility = if (demandes.isEmpty()) View.VISIBLE else View.GONE
        }

        // 4. Charger les demandes de l'utilisateur
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { viewModel.loadMesDemandes(it) }
    }

    private fun setupStatusFilter() {
        val statuses = listOf("Tous", "EN_ATTENTE", "ACCEPTEE", "REFUSEE")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatusFilter.adapter = spinnerAdapter

        binding.spinnerStatusFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (::adapter.isInitialized) {
                    val demandes = viewModel.mesDemandes.value ?: emptyList()
                    adapter.submitListWithFilter(demandes, getSelectedStatus())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getSelectedStatus(): DemandeStatus? {
        return when (binding.spinnerStatusFilter.selectedItem.toString()) {
            "EN_ATTENTE" -> DemandeStatus.EN_ATTENTE
            "ACCEPTEE" -> DemandeStatus.ACCEPTEE
            "REFUSEE" -> DemandeStatus.REFUSEE
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
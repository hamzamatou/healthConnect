package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.databinding.FragmentMesDemandesBinding
import com.example.healthproject.viewmodel.ParticipantMissionViewModel
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.viewmodel.ParticipantMissionViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MesDemandesFragment : Fragment() {

    private var _binding: FragmentMesDemandesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ParticipantMissionViewModel by viewModels {
        ParticipantMissionViewModelFactory(
            MissionRepository(),
            DemandeParticipationRepository()
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

        setupRecyclerView()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModel.loadMesDemandes(userId)
        }

        viewModel.mesDemandes.observe(viewLifecycleOwner) { demandes ->
            adapter.submitList(demandes)

            binding.tvEmpty.visibility =
                if (demandes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = MesDemandesAdapter()
        binding.recyclerViewDemandes.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerViewDemandes.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

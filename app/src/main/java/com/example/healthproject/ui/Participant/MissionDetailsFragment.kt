package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.RoleMission
import com.example.healthproject.databinding.FragmentMissionDetailsBinding
import com.example.healthproject.viewmodel.ParticipantMissionViewModel
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.viewmodel.ParticipantMissionViewModelFactory

class MissionDetailsFragment : Fragment() {

    private var _binding: FragmentMissionDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: MissionDetailsFragmentArgs by navArgs() // Nav Args pour missionId
    private val viewModel: ParticipantMissionViewModel by viewModels {
        ParticipantMissionViewModelFactory(MissionRepository(), DemandeParticipationRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roles = listOf("Infermier", "Médecin", "Superviseur")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = spinnerAdapter

        binding.spinnerRole.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                binding.etSpecialite.visibility = if (roles[position] == "Médecin") View.VISIBLE else View.GONE
                binding.etProfession.visibility = if (roles[position] == "Superviseur") View.VISIBLE else View.GONE
                binding.etCaracteristiques.visibility = if (roles[position] != "Médecin") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // rien à faire
            }
        }


        // Bouton participer
        binding.btnDemanderParticipation.setOnClickListener {
            val role = when (binding.spinnerRole.selectedItem.toString()) {
                "Infermier" -> RoleMission.INFIRMIER
                "Médecin" -> RoleMission.MEDECIN
                "Superviseur" -> RoleMission.SUPERVISEUR
                else -> RoleMission.VOLONTAIRE
            }

            viewModel.demanderParticipation(
                missionId = args.missionId,
                userId = "currentUserId", // récupérer l'ID courant
                roleMission = role,
                specialite = binding.etSpecialite.text.toString().takeIf { it.isNotEmpty() },
                profession = binding.etProfession.text.toString().takeIf { it.isNotEmpty() },
                caracteristiques = binding.etCaracteristiques.text.toString().takeIf { it.isNotEmpty() }
            ) { success, message ->
                if (success) {
                    Toast.makeText(requireContext(), "Demande envoyée !", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Erreur : $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

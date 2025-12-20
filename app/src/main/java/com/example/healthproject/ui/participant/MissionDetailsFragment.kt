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
import com.example.healthproject.data.model.RoleMission
import com.example.healthproject.databinding.FragmentMissionDetailsBinding
import com.example.healthproject.viewmodel.ParticipantMissionViewModel
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MessageRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.viewmodel.ParticipantMissionViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MissionDetailsFragment : Fragment() {

    private var _binding: FragmentMissionDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: MissionDetailsFragmentArgs by navArgs()

    private val viewModel: ParticipantMissionViewModel by viewModels {
        ParticipantMissionViewModelFactory(
            MissionRepository(),
            DemandeParticipationRepository(),
            MessageRepository()
        )
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

        //  Charger les détails de la mission
        loadMissionDetails()

        //  Spinner rôles
        val roles = listOf("Infermier", "Médecin", "Superviseur")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = spinnerAdapter

        //  Affichage dynamique des champs
        binding.spinnerRole.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.etSpecialite.visibility =
                        if (roles[position] == "Médecin") View.VISIBLE else View.GONE

                    binding.etProfession.visibility =
                        if (roles[position] == "Superviseur") View.VISIBLE else View.GONE

                    binding.etCaracteristiques.visibility =
                        if (roles[position] != "Médecin") View.VISIBLE else View.GONE
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            }

        //  Bouton participer
        binding.btnDemanderParticipation.setOnClickListener {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (currentUserId == null) {
                Toast.makeText(requireContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val role = when (binding.spinnerRole.selectedItem.toString()) {
                "Infermier" -> RoleMission.INFIRMIER
                "Médecin" -> RoleMission.MEDECIN
                "Superviseur" -> RoleMission.SUPERVISEUR
                else -> RoleMission.VOLONTAIRE
            }

            viewModel.demanderParticipation(
                missionId = args.missionId,
                userId = currentUserId,
                roleMission = role,
                specialite = binding.etSpecialite.text.toString().takeIf { it.isNotBlank() },
                profession = binding.etProfession.text.toString().takeIf { it.isNotBlank() },
                caracteristiques = binding.etCaracteristiques.text.toString().takeIf { it.isNotBlank() }
            ) { success, message ->
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Demande envoyée avec succès",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        message ?: "Erreur inconnue",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun loadMissionDetails() {
        viewModel.loadMissions()
        viewModel.missions.observe(viewLifecycleOwner) { missions ->
            val mission = missions.find { it.id == args.missionId }
            mission?.let {
                binding.tvMissionTitle.text = it.titre
                binding.tvMissionDescription.text = it.description
                binding.tvMissionDate.text =
                    "Du ${it.dateDebut} au ${it.dateFin}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

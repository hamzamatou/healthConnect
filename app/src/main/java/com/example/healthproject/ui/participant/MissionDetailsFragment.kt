package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.healthproject.data.model.RoleMission
import com.example.healthproject.data.model.Mission
import com.example.healthproject.databinding.FragmentMissionDetailsBinding
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MessageRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.viewmodel.ParticipantMissionViewModel
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupListeners()
        loadMissionDetails()
    }

    private fun setupSpinner() {
        val roles = listOf("Infirmier", "Médecin", "Superviseur")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = roles[position]
                binding.etSpecialite.visibility = if (selected == "Médecin") View.VISIBLE else View.GONE
                binding.etProfession.visibility = if (selected == "Superviseur") View.VISIBLE else View.GONE
                binding.etCaracteristiques.visibility = if (selected != "Médecin") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnVoulezParticiper.setOnClickListener {
            binding.layoutParticipation.visibility = View.VISIBLE
        }

        binding.btnDemanderParticipation.setOnClickListener {
            handleParticipation()
        }
    }

    private fun loadMissionDetails() {
        viewModel.loadMissions()
        viewModel.missions.observe(viewLifecycleOwner) { missions ->
            val mission = missions.find { it.id == args.missionId }
            mission?.let { displayMissionDetails(it) }
        }
    }

    private fun displayMissionDetails(m: Mission) {
        binding.tvMissionTitle.text = m.titre
        binding.tvMissionDescription.text = m.description
        binding.tvMissionLocationName.text = m.lieu

        val totalParticipants = m.nbrMedecin + m.nbrInfirmier + m.nbrVolontaire
        binding.tvGoingCount.text = "$totalParticipants Participants demandés"

        val sdfDate = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.FRENCH)
        val sdfTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH)

        binding.tvMissionDate.text = sdfDate.format(java.util.Date(m.dateDebut))
        binding.tvMissionTime.text = "De ${sdfTime.format(m.dateDebut)} à ${sdfTime.format(m.dateFin)}"

        m.imageBase64?.let {
            try {
                val bytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                binding.ivMissionHeader.setImageBitmap(bitmap)
            } catch (_: Exception) { }
        }
    }

    private fun handleParticipation() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            return
        }

        val role = when (binding.spinnerRole.selectedItem.toString()) {
            "Infirmier" -> RoleMission.INFIRMIER
            "Médecin" -> RoleMission.MEDECIN
            "Superviseur" -> RoleMission.SUPERVISEUR
            else -> RoleMission.VOLONTAIRE
        }

        viewModel.demanderParticipation(
            missionId = args.missionId,
            userId = userId,
            roleMission = role,
            specialite = binding.etSpecialite.text.toString().takeIf { it.isNotBlank() },
            profession = binding.etProfession.text.toString().takeIf { it.isNotBlank() },
            caracteristiques = binding.etCaracteristiques.text.toString().takeIf { it.isNotBlank() }
        ) { success, msg ->
            Toast.makeText(requireContext(), if (success) "Demande envoyée !" else msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

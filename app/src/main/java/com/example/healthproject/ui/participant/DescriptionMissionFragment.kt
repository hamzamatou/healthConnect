package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.databinding.FragmentDescriptionMissionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DescriptionMissionFragment : Fragment() {

    private var _binding: FragmentDescriptionMissionBinding? = null
    private val binding get() = _binding!!

    private val missionRepository = MissionRepository()
    private val args: DescriptionMissionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDescriptionMissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val missionId = args.missionId

        // 1️⃣ Charger les données de la mission
        chargerDonneesMission(missionId)

        // 2️⃣ Bouton Message
        binding.buttonMessage.setOnClickListener {
            val action =
                DescriptionMissionFragmentDirections
                    .actionDescriptionMissionFragmentToParticipantMessageFragment(missionId)
            findNavController().navigate(action)
        }

        // 3️⃣ Afficher / cacher le bouton Supervision
        binding.btnGoToSupervision.visibility =
            if (args.isSuperviseur) View.VISIBLE else View.GONE

        // 4️⃣ 🔥 NAVIGATION VERS DASHBOARD SUPERVISEUR
        binding.btnGoToSupervision.setOnClickListener {
            val action =
                DescriptionMissionFragmentDirections
                    .actionDescriptionMissionFragmentToSupervisionDashboardFragment(
                        missionId
                    )
            findNavController().navigate(action)
        }
    }


    private fun chargerDonneesMission(id: String) {
        missionRepository.getMissionById(id) { mission ->
            _binding?.let { b ->
                mission?.let { m ->
                    b.tvTitre.text = m.titre
                    b.tvDescription.text = m.description
                    // Ajoutez ici tvLieu, tvDateDebut si présents dans votre XML
                }
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        if (timestamp <= 0) return "--/--/----"
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
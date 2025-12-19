package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs // Import indispensable pour Safe Args
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.databinding.FragmentDescriptionMissionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.fragment.findNavController

class DescriptionMissionFragment : Fragment() {

    private var _binding: FragmentDescriptionMissionBinding? = null
    private val binding get() = _binding!!

    private val missionRepository = MissionRepository()

    // 1. Utilisation de Safe Args pour récupérer l'ID automatiquement
    // "DescriptionMissionFragmentArgs" est généré à partir de votre XML
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

        // 2. On récupère le missionId passé via la navigation
        val missionId = args.missionId

        // 3. Appel au repository pour récupérer les détails de la mission
        chargerDonneesMission(missionId)
        binding.buttonMessage.setOnClickListener {
            val action = DescriptionMissionFragmentDirections
                .actionDescriptionMissionFragmentToParticipantMessageFragment(missionId)
            findNavController().navigate(action)
        }
    }

    private fun chargerDonneesMission(id: String) {
        missionRepository.getMissionById(id) { mission ->
            // On vérifie que le binding est toujours disponible avant de mettre à jour la vue
            _binding?.let { b ->
                mission?.let { m ->
                    b.tvTitre.text = m.titre
                    b.tvDescription.text = m.description
                    b.tvLieu.text = m.lieu
                    b.tvDateDebut.text = formatDate(m.dateDebut)
                    b.tvDateFin.text = formatDate(m.dateFin)
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
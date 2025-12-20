package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.databinding.FragmentDescriptionMissionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DescriptionMissionFragment : Fragment() {

    private var _binding: FragmentDescriptionMissionBinding? = null
    private val binding get() = _binding!!

    private val missionRepository = MissionRepository()
    private val args: DescriptionMissionFragmentArgs by navArgs()

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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

        // Charger les données de la mission
        chargerDonneesMission(missionId)

        // Vérifier si la demande du user est acceptée pour afficher le bouton message
        verifierDemandeAcceptee(missionId, currentUserId)

        // Afficher / cacher le bouton Supervision selon le rôle
        binding.btnGoToSupervision.visibility =
            if (args.isSuperviseur) View.VISIBLE else View.GONE

        // Navigation vers la messagerie
        binding.buttonMessage.setOnClickListener {
            val action =
                DescriptionMissionFragmentDirections
                    .actionDescriptionMissionFragmentToParticipantMessageFragment(missionId)
            findNavController().navigate(action)
        }

        // Navigation vers le dashboard de supervision
        binding.btnGoToSupervision.setOnClickListener {
            val action =
                DescriptionMissionFragmentDirections
                    .actionDescriptionMissionFragmentToSupervisionDashboardFragment(missionId)
            findNavController().navigate(action)
        }

        // Bouton back
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun chargerDonneesMission(missionId: String) {
        missionRepository.getMissionById(missionId) { mission ->
            _binding?.let { b ->
                mission?.let { m ->
                    b.tvMissionTitle.text = m.titre
                    b.tvMissionDescription.text = m.description
                    b.tvMissionLocationName.text = m.lieu
                    b.tvMissionDate.text = formatDate(m.dateDebut)
                    b.tvMissionTime.text = formatDate(m.dateFin) // adapter selon ton besoin
                } ?: run {
                    b.tvMissionTitle.text = "Mission inconnue"
                    b.tvMissionDescription.text = "--"
                    b.tvMissionLocationName.text = "--"
                    b.tvMissionDate.text = "--/--/----"
                    b.tvMissionTime.text = "--/--/----"
                }
            }
        }
    }

    private fun verifierDemandeAcceptee(missionId: String, userId: String) {
        db.collection("demandesParticipation")
            .whereEqualTo("missionId", missionId)
            .whereEqualTo("userId", userId)
            .whereEqualTo("statut", DemandeStatus.ACCEPTEE.name)
            .get()
            .addOnSuccessListener { result ->
                binding.buttonMessage.visibility = if (!result.isEmpty) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                binding.buttonMessage.visibility = View.GONE
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

package com.example.healthproject.ui.superviseur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.healthproject.R
import com.example.healthproject.data.repository.AffectationMaterielRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.databinding.FragmentSupervisionDashboardBinding
import com.example.healthproject.viewmodel.SuperviseurViewModel
import com.example.healthproject.viewmodel.factory.SuperviseurViewModelFactory

class SupervisionDashboardFragment : Fragment() {

    private var _binding: FragmentSupervisionDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SuperviseurViewModel

    private val args: SupervisionDashboardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupervisionDashboardBinding.inflate(inflater, container, false)

        // Initialisation du ViewModel pour accéder à la fonction de clôture
        val factory = SuperviseurViewModelFactory(
            AffectationMaterielRepository(),
            DemandeParticipationRepository(),
            MissionRepository()
        )
        viewModel = ViewModelProvider(this, factory)[SuperviseurViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // Ajouté super

        val missionId = args.missionId

        binding.btnVerifierPresence.setOnClickListener {
            val action = SupervisionDashboardFragmentDirections.actionToPresence(missionId)
            findNavController().navigate(action)
        }

        binding.btnVerifierMateriel.setOnClickListener {
            val action = SupervisionDashboardFragmentDirections.actionToMateriel(missionId)
            findNavController().navigate(action)
        }


        binding.btnCloturerMission.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clôturer la mission")
                .setMessage("Êtes-vous sûr de vouloir clôturer cette mission ? Cette action est définitive.")
                .setPositiveButton("Oui") { _, _ ->
                    // Appeler le ViewModel pour changer le statut en "clôturée"
                    viewModel.cloturerMission(missionId)

                    // Bloquer le bouton visuellement
                    binding.btnCloturerMission.isEnabled = false
                    binding.btnCloturerMission.text = "Mission clôturée"
                    binding.btnCloturerMission.alpha = 0.5f
                }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
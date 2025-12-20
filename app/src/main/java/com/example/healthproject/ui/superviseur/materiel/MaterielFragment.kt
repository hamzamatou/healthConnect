package com.example.healthproject.ui.superviseur.materiel

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.data.repository.AffectationMaterielRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.viewmodel.SuperviseurViewModel
import com.example.healthproject.viewmodel.factory.SuperviseurViewModelFactory

class MaterielFragment : Fragment() {

    private lateinit var viewModel: SuperviseurViewModel
    private lateinit var missionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        missionId = arguments?.getString("missionId") ?: ""

        val affectationRepo = AffectationMaterielRepository()
        val participationRepo = DemandeParticipationRepository()
        val missionRepo = MissionRepository()

        val factory = SuperviseurViewModelFactory(
            affectationRepo,
            participationRepo,
            missionRepo
        )

        viewModel = ViewModelProvider(this, factory)[SuperviseurViewModel::class.java]

        // Charger la map
        viewModel.loadMaterielMap()

        // Observer la map pour charger les affectations après que la map soit prête
        viewModel.materielMap.observe(this) {
            viewModel.loadMateriel(missionId)
        }
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_materiel, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerMateriel)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val adapter = MaterielAdapter(viewModel)
        recycler.adapter = adapter

        viewModel.materielsAffectes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }



        return view
    }
}

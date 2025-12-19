package com.example.healthproject.ui.superviseur.materiel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.viewmodel.SuperviseurViewModel
import com.example.healthproject.viewmodel.factory.SuperviseurViewModelFactory

class MaterielFragment : Fragment() {

    private lateinit var viewModel: SuperviseurViewModel
    private lateinit var missionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        missionId = requireArguments().getString(ARG_MISSION_ID)!!

        viewModel = ViewModelProvider(
            requireActivity(),
            SuperviseurViewModelFactory()
        )[SuperviseurViewModel::class.java]
    }

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

        viewModel.materiels.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loadMateriel(missionId)

        return view
    }

    companion object {
        private const val ARG_MISSION_ID = "missionId"

        fun newInstance(missionId: String): MaterielFragment {
            return MaterielFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MISSION_ID, missionId)
                }
            }
        }
    }
}

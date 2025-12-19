package com.example.healthproject.ui.superviseur.presence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment   // ✅ IMPORTANT
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.viewmodel.SuperviseurViewModel
import com.example.healthproject.viewmodel.factory.SuperviseurViewModelFactory

class PresenceFragment : Fragment() {

    private lateinit var viewModel: SuperviseurViewModel
    private lateinit var missionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        missionId = requireArguments().getString("missionId")!!

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
        val view = inflater.inflate(R.layout.fragment_presence, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerPresence)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PresenceAdapter(viewModel)
        recycler.adapter = adapter

        viewModel.participants.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loadParticipants(missionId)

        return view
    }

    companion object {
        fun newInstance(missionId: String): PresenceFragment {
            return PresenceFragment().apply {
                arguments = Bundle().apply {
                    putString("missionId", missionId)
                }
            }
        }
    }
}

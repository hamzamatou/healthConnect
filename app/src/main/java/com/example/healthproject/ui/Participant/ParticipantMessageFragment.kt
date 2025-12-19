package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.data.model.Message
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MessageRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.databinding.FragmentMessageBinding
import com.example.healthproject.viewmodel.ParticipantMissionViewModel
import com.example.healthproject.viewmodel.ParticipantMissionViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class ParticipantMessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MessageAdapter
    private lateinit var viewModel: ParticipantMissionViewModel

    private lateinit var missionId: String
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        missionId = arguments?.getString("MISSION_ID") ?: return
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        adapter = MessageAdapter(userId)
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMessages.adapter = adapter

        // ViewModel
        viewModel = ViewModelProvider(
            this,
            ParticipantMissionViewModelFactory(
                MissionRepository(),
                DemandeParticipationRepository(),
                MessageRepository()
            )
        )[ParticipantMissionViewModel::class.java]
        // Observer messages
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
            binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
        }

        // Écoute en temps réel
        viewModel.listenMessages(missionId)

        // Envoyer message
        binding.buttonSend.setOnClickListener {
            val text = binding.editTextMessage.text.toString()
            if (text.isNotEmpty()) {
                val message = Message(
                    missionId = missionId,
                    senderId = userId,
                    contenu = text
                )
                viewModel.sendMessage(message)
                binding.editTextMessage.text.clear()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

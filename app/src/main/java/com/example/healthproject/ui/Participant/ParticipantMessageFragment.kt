package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.data.model.Message
import com.example.healthproject.databinding.FragmentMessageBinding
import com.example.healthproject.viewmodel.ParticipantMissionViewModel
import com.example.healthproject.ui.Participant.MessageAdapter
import com.google.firebase.auth.FirebaseAuth

class ParticipantMessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ParticipantMissionViewModel
    private lateinit var adapter: MessageAdapter

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
        missionId = arguments?.getString("MISSION_ID") ?: ""

        // Récupérer l'utilisateur connecté depuis Firebase Auth
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        } else {
            // L'utilisateur n'est pas connecté
            return
        }

        viewModel = ViewModelProvider(this).get(ParticipantMissionViewModel::class.java)

        adapter = MessageAdapter(userId)
        binding.recyclerViewMessages.adapter = adapter
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(context)

        // Vérifier si le participant peut envoyer des messages
        viewModel.checkDemandeStatus(missionId, userId)

        // Observer si l’utilisateur peut envoyer un message
        viewModel.canSend.observe(viewLifecycleOwner) { canSend ->
            binding.editTextMessage.isEnabled = canSend
            binding.buttonSend.isEnabled = canSend
        }

        // Observer les messages et mettre à jour l’adapter
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
            binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
        }

        // Bouton envoyer
        binding.buttonSend.setOnClickListener {
            val content = binding.editTextMessage.text.toString()
            if (content.isNotEmpty() && viewModel.canSend.value == true) {
                val message = Message(
                    missionId = missionId,
                    senderId = userId,
                    contenu = content
                )
                viewModel.sendMessage(message)
                binding.editTextMessage.text.clear()
            }
        }

        // Charger les messages
        viewModel.loadMessages(missionId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

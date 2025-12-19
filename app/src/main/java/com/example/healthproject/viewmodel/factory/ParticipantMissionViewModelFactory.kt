package com.example.healthproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MessageRepository
import com.example.healthproject.data.repository.MissionRepository

class ParticipantMissionViewModelFactory(
    private val missionRepository: MissionRepository,
    private val demandeRepository: DemandeParticipationRepository,
    private val messageRepository: MessageRepository  // <-- ajouter la virgule avant
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParticipantMissionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParticipantMissionViewModel(missionRepository, demandeRepository, messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

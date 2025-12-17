package com.example.healthproject.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.AffectationMaterielRepository
import com.example.healthproject.viewmodel.CoordinateurMissionViewModel

class CoordinateurMissionViewModelFactory(
    private val missionRepository: MissionRepository,
    private val demandeRepository: DemandeParticipationRepository,
    private val affectationRepository: AffectationMaterielRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoordinateurMissionViewModel::class.java)) {
            return CoordinateurMissionViewModel(
                missionRepository,
                demandeRepository,
                affectationRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

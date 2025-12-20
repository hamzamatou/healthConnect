package com.example.healthproject.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthproject.data.repository.AffectationMaterielRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.viewmodel.SuperviseurViewModel

class SuperviseurViewModelFactory(
    private val affectationRepo: AffectationMaterielRepository,
    private val participationRepo: DemandeParticipationRepository,
    private val missionRepo: MissionRepository   // ✅ AJOUT
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuperviseurViewModel::class.java)) {
            return SuperviseurViewModel(
                affectationRepo,
                participationRepo,
                missionRepo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

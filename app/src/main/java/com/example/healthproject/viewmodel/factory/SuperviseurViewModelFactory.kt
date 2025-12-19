package com.example.healthproject.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthproject.data.repository.AffectationMaterielRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.viewmodel.SuperviseurViewModel

class SuperviseurViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SuperviseurViewModel(
            DemandeParticipationRepository(),
            AffectationMaterielRepository()
        ) as T
    }
}

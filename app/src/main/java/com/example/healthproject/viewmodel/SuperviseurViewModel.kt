package com.example.healthproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthproject.data.model.AffectationMateriel
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.repository.AffectationMaterielRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
class SuperviseurViewModel(
    private val demandeRepo: DemandeParticipationRepository,
    private val materielRepo: AffectationMaterielRepository
) : ViewModel() {

    val participants = MutableLiveData<List<DemandeParticipation>>()
    val materiels = MutableLiveData<List<AffectationMateriel>>()

    fun loadParticipants(missionId: String) {
        demandeRepo.getParticipantsForMission(missionId) {
            participants.postValue(it)
        }
    }

    fun markPresence(demandeId: String, present: Boolean) {
        demandeRepo.updatePresence(demandeId, present)
    }

    fun loadMateriel(missionId: String) {
        materielRepo.getAffectationsByMission(missionId) {
            materiels.postValue(it)
        }
    }

    fun updateEtatMateriel(id: String, etat: String) {
        materielRepo.updateEtatApres(id, etat)
    }
}

package com.example.healthproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthproject.data.model.*
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository

class ParticipantMissionViewModel(
    private val missionRepository: MissionRepository,
    private val demandeRepository: DemandeParticipationRepository
) : ViewModel() {

    private val _missions = MutableLiveData<List<Mission>>()
    val missions: LiveData<List<Mission>> get() = _missions

    private val _mesDemandes = MutableLiveData<List<DemandeParticipation>>()
    val mesDemandes: LiveData<List<DemandeParticipation>> get() = _mesDemandes

    private val _demandeStatus = MutableLiveData<DemandeStatus?>()
    val demandeStatus: LiveData<DemandeStatus?> get() = _demandeStatus

    // Charger toutes les missions
    fun loadMissions() {
        missionRepository.getAllMissions { _missions.postValue(it) }
    }

    // Créer une demande de participation
    fun demanderParticipation(
        missionId: String,
        userId: String,
        roleMission: RoleMission,
        specialite: String? = null,
        profession: String? = null,
        caracteristiques: String? = null,
        callback: (Boolean, String?) -> Unit
    ) {
        val demande = DemandeParticipation(
            missionId = missionId,
            userId = userId,
            roleMission = roleMission,
            specialite = specialite,
            profession = profession,
            caracteristiques = caracteristiques,
            statut = DemandeStatus.EN_ATTENTE
        )

        demandeRepository.createDemande(demande, callback)
    }

    // Charger MES demandes
    fun loadMesDemandes(userId: String) {
        val allStatuses = DemandeStatus.values()
        val result = mutableListOf<DemandeParticipation>()
        var count = 0
        for (status in allStatuses) {
            demandeRepository.getDemandesByParticipantAndStatus(userId, status) { list ->
                result.addAll(list)
                count++
                if (count == allStatuses.size) _mesDemandes.postValue(result)
            }
        }
    }

    // Vérifier statut pour UNE mission
    fun checkDemandeStatus(missionId: String, userId: String) {
        demandeRepository.getDemandeByMissionAndUser(missionId, userId) { demande ->
            _demandeStatus.postValue(demande?.statut)
        }
    }

    // Bouton participer visible ?
    fun isParticipationAllowed(): Boolean {
        return _demandeStatus.value == null || _demandeStatus.value == DemandeStatus.REFUSEE
    }
}

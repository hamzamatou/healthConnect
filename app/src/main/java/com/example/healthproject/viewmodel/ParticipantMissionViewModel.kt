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

    // Liste des missions
    private val _missions = MutableLiveData<List<Mission>>()
    val missions: LiveData<List<Mission>> get() = _missions

    // Map missionId -> Mission pour un accès rapide
    val missionsMap: Map<String, Mission>
        get() = _missions.value?.associateBy { it.id ?: "" } ?: emptyMap()

    // Liste des demandes de l'utilisateur
    private val _mesDemandes = MutableLiveData<List<DemandeParticipation>>()
    val mesDemandes: LiveData<List<DemandeParticipation>> get() = _mesDemandes

    // Statut d'une demande spécifique
    private val _demandeStatus = MutableLiveData<DemandeStatus?>()
    val demandeStatus: LiveData<DemandeStatus?> get() = _demandeStatus

    // ------------------ Missions ------------------

    // Charger toutes les missions depuis Firestore
    fun loadMissions() {
        missionRepository.getAllMissions { _missions.postValue(it) }
    }

    // Vérifier si la participation à une mission est possible
    fun canParticipateToMission(missionId: String): Boolean {
        val mission = missionsMap[missionId] ?: return false
        return mission.statut != MissionStatus.EN_COURS &&
                mission.statut != MissionStatus.CLOTUREE &&
                mission.statut != MissionStatus.ANNULE
    }

    // ------------------ Demandes ------------------

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
        if (!canParticipateToMission(missionId)) {
            callback(false, "Impossible de participer à cette mission (en cours, clôturée ou annulée)")
            return
        }

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

    // Charger toutes les demandes de l'utilisateur
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

    // Vérifier le statut d'une demande pour une mission donnée
    fun checkDemandeStatus(missionId: String, userId: String) {
        demandeRepository.getDemandeByMissionAndUser(missionId, userId) { demande ->
            _demandeStatus.postValue(demande?.statut)
        }
    }

    // Vérifier si le bouton "Participer" peut être affiché
    fun isParticipationAllowed(): Boolean {
        return (_demandeStatus.value == null || _demandeStatus.value == DemandeStatus.REFUSEE)
    }
}

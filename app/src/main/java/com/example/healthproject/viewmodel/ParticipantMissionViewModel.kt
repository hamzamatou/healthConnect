package com.example.healthproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthproject.data.model.*
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.data.repository.MessageRepository

class ParticipantMissionViewModel(
    private val missionRepository: MissionRepository,
    private val demandeRepository: DemandeParticipationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _missions = MutableLiveData<List<Mission>>()
    val missions: LiveData<List<Mission>> get() = _missions
    val missionsMap: Map<String, Mission>
        get() = _missions.value?.associateBy { it.id ?: "" } ?: emptyMap()

    private val _mesDemandes = MutableLiveData<List<DemandeParticipation>>()
    val mesDemandes: LiveData<List<DemandeParticipation>> get() = _mesDemandes

    private val _demandeStatus = MutableLiveData<DemandeStatus?>()
    val demandeStatus: LiveData<DemandeStatus?> get() = _demandeStatus

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _canSend = MutableLiveData<Boolean>()
    val canSend: LiveData<Boolean> get() = _canSend

    fun loadMissions() = missionRepository.getAllMissions { _missions.postValue(it) }

    fun canParticipateToMission(missionId: String) =
        missionsMap[missionId]?.statut !in listOf(MissionStatus.EN_COURS, MissionStatus.CLOTUREE, MissionStatus.ANNULE)

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
            callback(false, "Impossible de participer à cette mission")
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

    fun loadMesDemandes(userId: String) {
        val allStatuses = DemandeStatus.values()
        val result = mutableListOf<DemandeParticipation>()
        var count = 0
        allStatuses.forEach { status ->
            demandeRepository.getDemandesByParticipantAndStatus(userId, status) { list ->
                result.addAll(list)
                count++
                if (count == allStatuses.size) _mesDemandes.postValue(result)
            }
        }
    }

    fun checkDemandeStatus(missionId: String, userId: String) =
        demandeRepository.getDemandeByMissionAndUser(missionId, userId) { demande ->
            _demandeStatus.postValue(demande?.statut)
            _canSend.postValue(demande?.statut == DemandeStatus.ACCEPTEE)
        }

    fun isParticipationAllowed() = _demandeStatus.value == null || _demandeStatus.value == DemandeStatus.REFUSEE

    fun sendMessage(message: Message) {
        if (_canSend.value == true) {
            messageRepository.sendMessage(message) { success, error ->
                if (!success) {
                    // ici tu peux log ou afficher une erreur
                }
            }
        }
    }

    fun loadMessages(missionId: String) =
        messageRepository.getMessagesByMission(missionId) { _messages.postValue(it) }
}

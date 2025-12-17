package com.example.healthproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthproject.data.model.*
import com.example.healthproject.data.repository.MissionRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.AffectationMaterielRepository

class CoordinateurMissionViewModel(
    private val missionRepository: MissionRepository,
    private val demandeRepository: DemandeParticipationRepository,
    private val affectationRepository: AffectationMaterielRepository
) : ViewModel() {

    // Liste des missions créées
    private val _missions = MutableLiveData<List<Mission>>()
    val missions: LiveData<List<Mission>> get() = _missions

    // Liste des demandes de participation (toutes les demandes pour toutes les missions)
    private val _demandes = MutableLiveData<List<DemandeParticipation>>()
    val demandes: LiveData<List<DemandeParticipation>> get() = _demandes

    // Création d'une mission
    fun createMission(mission: Mission, callback: (Boolean, String?) -> Unit) {
        missionRepository.createMission(mission) { success, message ->
            if (success) getAllMissions()
            callback(success, message)
        }
    }

    // Récupérer toutes les missions
    fun getAllMissions() {
        missionRepository.getAllMissions { missionList ->
            _missions.postValue(missionList)
        }
    }

    // Récupérer toutes les demandes de participation pour une mission donnée
    fun getDemandesByMission(missionId: String, status: DemandeStatus? = null) {
        if (status != null) {
            // Filtrage par status
            demandeRepository.getDemandesByMissionAndStatus(missionId, status) { demandeList ->
                _demandes.postValue(demandeList)
            }
        } else {
            // Toutes les demandes de la mission
            val statuses = DemandeStatus.values()
            val allDemandes = mutableListOf<DemandeParticipation>()
            var count = 0
            for (s in statuses) {
                demandeRepository.getDemandesByMissionAndStatus(missionId, s) { list ->
                    allDemandes.addAll(list)
                    count++
                    if (count == statuses.size) _demandes.postValue(allDemandes)
                }
            }
        }
    }

    // Accepter une demande
    fun acceptDemande(demande: DemandeParticipation, callback: (Boolean, String?) -> Unit) {
        demandeRepository.updateDemandeStatus(demande.id ?: "", DemandeStatus.ACCEPTEE) { success, message ->
            if (success) getDemandesByMission(demande.missionId)
            callback(success, message)
        }
    }

    // Refuser une demande
    fun refuseDemande(demande: DemandeParticipation, callback: (Boolean, String?) -> Unit) {
        demandeRepository.updateDemandeStatus(demande.id ?: "", DemandeStatus.REFUSEE) { success, message ->
            if (success) getDemandesByMission(demande.missionId)
            callback(success, message)
        }
    }

    // Affecter un matériel à une mission
    fun affecterMateriel(missionId: String, materiel: Materiel, quantite: Int, callback: (Boolean, String?) -> Unit) {
        val affectation = AffectationMateriel(
            id = null,
            missionId = missionId,
            materielId = materiel.id ?: "",
            quantiteAffectee = quantite
        )
        affectationRepository.affecterMateriel(affectation) { success, message ->
            callback(success, message)
        }
    }

    // Récupérer les matériels affectés à une mission
    fun getMaterielsAffectes(missionId: String, callback: (List<AffectationMateriel>) -> Unit) {
        affectationRepository.getAffectationsByMission(missionId) { liste ->
            callback(liste)
        }
    }
}

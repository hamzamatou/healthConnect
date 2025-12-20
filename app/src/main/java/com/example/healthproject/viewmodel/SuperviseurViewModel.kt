package com.example.healthproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthproject.data.model.AffectationMateriel
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.Materiel
import com.example.healthproject.data.model.MissionStatus
import com.example.healthproject.data.repository.AffectationMaterielRepository
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.data.repository.MissionRepository

class SuperviseurViewModel(
    private val affectationRepo: AffectationMaterielRepository,
    private val participationRepo: DemandeParticipationRepository,
    private val missionRepo: MissionRepository   // ✅ AJOUT

) : ViewModel() {

    private val _materielsAffectes = MutableLiveData<List<AffectationMateriel>>()
    val materielsAffectes: LiveData<List<AffectationMateriel>> = _materielsAffectes

    private val _materielMap = MutableLiveData<Map<String, Materiel>>()
    val materielMap: LiveData<Map<String, Materiel>> = _materielMap

    private val _participations = MutableLiveData<List<DemandeParticipation>>()
    val participations: LiveData<List<DemandeParticipation>> = _participations

    // Charger la map des matériels
    fun loadMaterielMap() {
        affectationRepo.getAllMateriels { materiels ->
            _materielMap.postValue(materiels.associateBy { it.id ?: "" })
        }
    }

    // Charger les affectations et remplir le nom
    fun loadMateriel(missionId: String) {
        affectationRepo.getAffectationsByMission(missionId) { list ->
            val map = _materielMap.value ?: emptyMap()
            val listWithName = list.map { affect ->
                affect.copy(nomMateriel = map[affect.materielId]?.nom ?: "")
            }.filter { it.quantiteAffectee > 0 }
            _materielsAffectes.postValue(listWithName)
        }
    }

    // Mettre à jour état et quantité après (une seule fois)
    fun updateEtatMateriel(
        affectation: AffectationMateriel,
        etat: String,
        quantiteApresSaisie: Int,
        onComplete: () -> Unit
    ) {
        val quantiteFinale = quantiteApresSaisie.coerceAtMost(affectation.quantiteAffectee)

        affectationRepo.updateEtatApres(affectation.id!!, etat, quantiteFinale)

        _materielMap.value?.get(affectation.materielId)?.let { materiel ->
            val nouvelleQuantite = materiel.quantiteInitiale + quantiteFinale
            affectationRepo.updateQuantiteInitiale(materiel.id!!, nouvelleQuantite)
        }

        val currentList = _materielsAffectes.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.id == affectation.id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(etatApres = etat, quantiteApres = quantiteFinale)
        }
        _materielsAffectes.value = currentList

        onComplete()
    }

    // -----------------------------
    // Gérer la présence des participants
    fun markPresence(participationId: String, present: Boolean) {
        participationRepo.markPresence(participationId, present) { success ->
            if (success) {
                val currentList = _participations.value?.toMutableList() ?: mutableListOf()
                val index = currentList.indexOfFirst { it.id == participationId }
                if (index != -1) {
                    currentList.removeAt(index)
                    _participations.value = currentList
                }
            }
        }
    }

    fun loadParticipants(missionId: String) {
        participationRepo.getParticipantsForMission(missionId) { list ->
            _participations.postValue(list)
        }
    }
    fun cloturerMission(missionId: String) {
        missionRepo.updateMissionStatus(
            missionId,
            MissionStatus.CLOTUREE // ✅ statut CLOTUREE
        )
    }



}

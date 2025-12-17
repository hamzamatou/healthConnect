package com.example.healthproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthproject.data.repository.AuthRepository

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // Champs saisis par l'utilisateur
    var nom: String = ""
    var prenom: String = ""
    var email: String = ""
    var password: String = ""
    var cin: String = ""
    var adresse: String = ""
    var numeroTelephone: String = ""

    // LiveData pour observer le résultat d'authentification
    val authResult = MutableLiveData<Pair<Boolean, String?>>()

    /**
     * Inscription
     */
    fun register() {
        repository.register(
            nom = nom,
            prenom = prenom,
            email = email,
            password = password,
            cin = cin,
            adresse = adresse,
            numeroTelephone = numeroTelephone
        ) { success, message ->
            authResult.postValue(Pair(success, message))
        }
    }

    /**
     * Connexion
     */
    fun login() {
        repository.login(email, password) { success, message ->
            authResult.postValue(Pair(success, message))
        }
    }
}

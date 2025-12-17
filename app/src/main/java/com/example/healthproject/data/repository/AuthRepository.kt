package com.example.healthproject.data.repository

import com.example.healthproject.data.model.User
import com.example.healthproject.data.model.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Register a new user (PARTICIPANT par défaut)
     */
    fun register(
        nom: String,
        prenom: String,
        email: String,
        password: String,
        cin: String,
        adresse: String,
        numeroTelephone: String,
        callback: (Boolean, String?) -> Unit
    ) {
        // 🔹 Vérifications simples
        if (email.isBlank() || password.isBlank()) {
            callback(false, "Email et mot de passe obligatoires")
            return
        }

        if (cin.isBlank()) {
            callback(false, "CIN obligatoire")
            return
        }

        // 🔹 Création Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val userId = result.user?.uid ?: return@addOnSuccessListener

                // 🔹 Création Firestore User
                val user = User(
                    id = userId,
                    nom = nom,
                    prenom = prenom,
                    email = email,
                    cin = cin,
                    adresse = adresse,
                    numeroTelephone = numeroTelephone,
                    type = UserType.PARTICIPANT // par défaut
                )

                db.collection("users")
                    .document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        callback(true, null)
                    }
                    .addOnFailureListener { e ->
                        callback(false, e.message)
                    }

            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    /**
     * Login
     */
    fun login(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    /**
     * Get current logged user
     */
    fun getCurrentUser() = auth.currentUser
}

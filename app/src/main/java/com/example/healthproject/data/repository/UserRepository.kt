package com.example.healthproject.data.repository

import com.example.healthproject.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getUserById(userId: String, callback: (User?) -> Unit) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObject(User::class.java))
            }
            .addOnFailureListener { _ -> callback(null) }
    }
}

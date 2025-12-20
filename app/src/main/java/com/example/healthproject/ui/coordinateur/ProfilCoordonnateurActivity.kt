package com.example.healthproject.ui.coordinateur

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.R
import com.example.healthproject.data.model.User
import com.example.healthproject.databinding.ActivityProfilCoordonnateurBinding
import com.example.healthproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilCoordonnateurActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilCoordonnateurBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilCoordonnateurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject(User::class.java)
                    user?.let { afficherProfil(it) }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show()
                }
        }

        setupBottomNavigation()
    }

    private fun afficherProfil(user: User) {
        binding.apply {
            // Header
            textNomPrenomHeader.text = "${user.nom} ${user.prenom}"

            // Valeurs des champs
            textNomValue.text = user.nom
            textPrenomValue.text = user.prenom
            textCINValue.text = user.cin
            textEmailValue.text = user.email
            textAdresseValue.text = user.adresse
            textNumeroValue.text = user.numeroTelephone
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, CoordinateurMissionsActivity::class.java))
                    true
                }
                R.id.nav_profile -> true
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
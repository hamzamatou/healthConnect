package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.model.User
import com.example.healthproject.databinding.ActivityProfilCoordonnateurBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.healthproject.R
import com.example.healthproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ProfilCoordonnateurActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilCoordonnateurBinding
    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilCoordonnateurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = "ID_COORDONNATEUR" // Remplacer par ID de l'utilisateur connecté
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                user?.let { afficherProfil(it) }
            }
        binding.bottomNavigation.selectedItemId = R.id.nav_profile

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, CoordinateurMissionsActivity::class.java))
                    true
                }

                R.id.nav_profile -> {
                    // Ici on est déjà sur Profil, donc rien à faire
                    true
                }

                R.id.nav_logout -> {
                    logout()
                    true
                }

                else -> false
            }
        }

    }

    private fun afficherProfil(user: User) {
        binding.textNomPrenom.text = "${user.nom} ${user.prenom}"
        binding.textTypeUser.text = if (user.type.name == "COORDINATEUR") "Coordonnateur" else "Utilisateur"
        binding.textCIN.text = user.cin
        binding.textEmail.text = user.email
        binding.textAdresse.text = user.adresse
        binding.textNumero.text = user.numeroTelephone
    }
}

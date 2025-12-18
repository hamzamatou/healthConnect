package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.model.User
import com.example.healthproject.databinding.ActivityProfilCoordonnateurBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.example.healthproject.R

class ProfilCoordonnateurActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilCoordonnateurBinding

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
                    startActivity(
                        Intent(this, CoordinateurMissionsActivity::class.java)
                    )
                    true
                }

                R.id.nav_profile -> true

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

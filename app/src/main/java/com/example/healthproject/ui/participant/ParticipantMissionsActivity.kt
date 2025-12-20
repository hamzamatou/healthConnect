package com.example.healthproject.ui.participant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.healthproject.R
import com.example.healthproject.databinding.ActivityParticipantMissionsBinding
import com.example.healthproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.bottomnavigation.BottomNavigationView

class ParticipantMissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParticipantMissionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantMissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurer la Toolbar
        setSupportActionBar(binding.toolbar)

        // Récupérer le NavController
        val navController = findNavController(R.id.navHostFragmentParticipant)

        // Lier Toolbar avec NavController
        setupActionBarWithNavController(navController)

        // Lier BottomNavigationView avec NavController
        binding.bottomNavParticipant.setupWithNavController(navController)

        // Ajouter le listener pour le logout
        binding.bottomNavParticipant.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> {
                    // Laisser le NavController gérer les autres items
                    navController.navigate(item.itemId)
                    true
                }
            }
        }
    }

    // Gestion du retour en arrière avec NavController
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragmentParticipant)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Fonction pour se déconnecter
    private fun logout() {
        FirebaseAuth.getInstance().signOut() // Déconnexion Firebase
        Toast.makeText(this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show()

        // Redirection vers LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

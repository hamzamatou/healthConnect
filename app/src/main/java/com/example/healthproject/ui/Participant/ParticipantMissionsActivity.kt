package com.example.healthproject.ui.participant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.healthproject.R
import com.example.healthproject.databinding.ActivityParticipantMissionsBinding

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
    }

    // Gestion du retour en arrière avec NavController
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragmentParticipant)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

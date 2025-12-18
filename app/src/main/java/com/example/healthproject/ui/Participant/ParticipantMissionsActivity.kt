package com.example.healthproject.ui.participant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.healthproject.R
import com.example.healthproject.databinding.ActivityParticipantMissionsBinding

class ParticipantMissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParticipantMissionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantMissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurer la toolbar pour la navigation
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.navHostFragmentParticipant)
        setupActionBarWithNavController(navController)
    }

    // Permet le retour en arrière avec le NavController
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragmentParticipant)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

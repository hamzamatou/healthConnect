package com.example.healthproject.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.repository.AuthRepository
import com.example.healthproject.databinding.ActivityRegisterBinding
import com.example.healthproject.ui.login.LoginActivity
import com.example.healthproject.viewmodel.AuthViewModel
import com.example.healthproject.viewmodel.AuthViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bouton Register
        binding.btnRegister.setOnClickListener {
            viewModel.nom = binding.etNom.text.toString()
            viewModel.prenom = binding.etPrenom.text.toString()
            viewModel.cin = binding.etCin.text.toString()
            viewModel.adresse = binding.etAdresse.text.toString()
            viewModel.numeroTelephone = binding.etNumero.text.toString()
            viewModel.email = binding.etEmail.text.toString()
            viewModel.password = binding.etPassword.text.toString()

            viewModel.register()
        }

        // Observer le résultat
        viewModel.authResult.observe(this) { (success, message) ->
            if (success) {
                Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Erreur: $message", Toast.LENGTH_SHORT).show()
            }
        }

        // Lien vers Login
        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

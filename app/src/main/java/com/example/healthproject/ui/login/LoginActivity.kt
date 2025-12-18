package com.example.healthproject.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.repository.AuthRepository
import com.example.healthproject.databinding.ActivityLoginBinding
import com.example.healthproject.MainActivity
import com.example.healthproject.data.model.UserType
import com.example.healthproject.ui.coordinateur.CoordinateurMissionsActivity
import com.example.healthproject.ui.participant.ParticipantMissionsActivity
import com.example.healthproject.ui.register.RegisterActivity
import com.example.healthproject.viewmodel.AuthViewModel
import com.example.healthproject.viewmodel.factory.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            viewModel.email = binding.etEmail.text.toString()
            viewModel.password = binding.etPassword.text.toString()
            viewModel.login()
        }

        viewModel.authResult.observe(this) { (success, message) ->
            if (success) {
                viewModel.getUserType { type, error ->
                    if (type != null) {
                        when (type) {
                            UserType.COORDINATEUR -> {
                                startActivity(
                                    Intent(this, CoordinateurMissionsActivity::class.java)
                                )
                            }
                            UserType.PARTICIPANT -> {
                                startActivity(
                                    Intent(this, ParticipantMissionsActivity::class.java)
                                )
                            }
                        }
                        finish()
                    } else {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Erreur: $message", Toast.LENGTH_SHORT).show()
            }
        }


        // Lien vers Register
        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}

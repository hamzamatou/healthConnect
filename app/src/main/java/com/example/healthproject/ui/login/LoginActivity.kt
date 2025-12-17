package com.example.healthproject.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.healthproject.data.repository.AuthRepository
import com.example.healthproject.databinding.ActivityLoginBinding
import com.example.healthproject.MainActivity
import com.example.healthproject.ui.register.RegisterActivity
import com.example.healthproject.viewmodel.AuthViewModel
import com.example.healthproject.viewmodel.AuthViewModelFactory

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
                Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
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

package com.example.healthproject.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthproject.data.model.User
import com.example.healthproject.databinding.FragmentProfilParticipantBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilParticipantFragment : Fragment() {

    private var _binding: FragmentProfilParticipantBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilParticipantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chargerProfil()
    }

    private fun chargerProfil() {
        val currentUser = auth.currentUser ?: return

        db.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                user?.let { afficherProfil(it) }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erreur de chargement du profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun afficherProfil(user: User) {
        binding.apply {
            textNomPrenomHeader.text = "${user.nom} ${user.prenom}"

            textNomValue.text = user.nom
            textPrenomValue.text = user.prenom
            textCINValue.text = user.cin
            textEmailValue.text = user.email
            textNumeroValue.text = user.numeroTelephone
            textAdresseValue.text = user.adresse
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

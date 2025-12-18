package com.example.healthproject.ui.coordinateur.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.User
import com.example.healthproject.databinding.ItemRequestParticipantBinding
import com.google.firebase.firestore.FirebaseFirestore

class ParticipantsAdapter : RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder>() {

    private var participants: List<DemandeParticipation> = listOf()

    fun setParticipants(list: List<DemandeParticipation>) {
        participants = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val binding = ItemRequestParticipantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ParticipantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(participants[position])
    }

    override fun getItemCount(): Int = participants.size

    inner class ParticipantViewHolder(private val binding: ItemRequestParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: DemandeParticipation) {
            binding.textViewRole.text = "Rôle: ${participant.roleMission}"

            // Nom par défaut
            binding.textViewNom.text = "Chargement…"

            // Charger nom complet depuis Firestore
            if (!participant.userId.isNullOrEmpty()) {
                FirebaseFirestore.getInstance().collection("users")
                    .document(participant.userId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject(User::class.java)
                        binding.textViewNom.text = user?.let { "${it.nom} ${it.prenom}" }
                            ?: "Utilisateur inconnu"
                    }
                    .addOnFailureListener {
                        binding.textViewNom.text = "Erreur de chargement"
                    }
            } else {
                binding.textViewNom.text = "Utilisateur inconnu"
            }

            // Dans cette liste, les boutons Confirmer/Annuler sont cachés
            binding.btnConfirm.visibility = android.view.View.GONE
            binding.btnCancel.visibility = android.view.View.GONE
        }
    }
}

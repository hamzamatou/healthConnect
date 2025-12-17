package com.example.healthproject.ui.coordinateur.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.databinding.ItemParticipantBinding

class ParticipantsAdapter : RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder>() {

    private var participants: List<DemandeParticipation> = listOf()

    fun setParticipants(list: List<DemandeParticipation>) {
        participants = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val binding = ItemParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParticipantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(participants[position])
    }

    override fun getItemCount(): Int = participants.size

    inner class ParticipantViewHolder(private val binding: ItemParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(demande: DemandeParticipation) {
            binding.textViewNom.text = "ID utilisateur : ${demande.userId}"
            binding.textViewRole.text = "Rôle : ${demande.roleMission}"
        }
    }
}

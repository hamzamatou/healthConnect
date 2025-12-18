package com.example.healthproject.ui.participant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.databinding.ItemDemandeBinding

class MesDemandesAdapter :
    ListAdapter<DemandeParticipation, MesDemandesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDemandeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemDemandeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(demande: DemandeParticipation) {

            binding.tvRole.text = demande.roleMission.name

            binding.tvStatus.text = demande.statut.name

            val statusColor = when (demande.statut) {
                DemandeStatus.EN_ATTENTE -> android.R.color.holo_orange_dark
                DemandeStatus.ACCEPTEE -> android.R.color.holo_green_dark
                DemandeStatus.REFUSEE -> android.R.color.holo_red_dark
            }

            binding.tvStatus.setTextColor(
                binding.root.context.getColor(statusColor)
            )
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DemandeParticipation>() {
        override fun areItemsTheSame(
            oldItem: DemandeParticipation,
            newItem: DemandeParticipation
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: DemandeParticipation,
            newItem: DemandeParticipation
        ) = oldItem == newItem
    }
}

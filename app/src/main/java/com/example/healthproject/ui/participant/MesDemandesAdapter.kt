package com.example.healthproject.ui.participant

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.data.model.Mission
import com.example.healthproject.databinding.ItemDemandeBinding

class MesDemandesAdapter(
    private val missionsMap: Map<String, Mission> = emptyMap()
) : ListAdapter<DemandeParticipation, MesDemandesAdapter.ViewHolder>(DiffCallback()) {

    var onDetailClickListener: ((Mission) -> Unit)? = null
    private var filteredList: List<DemandeParticipation> = emptyList()

    fun submitListWithFilter(list: List<DemandeParticipation>, statusFilter: DemandeStatus? = null) {
        filteredList = if (statusFilter != null) {
            list.filter { it.statut == statusFilter }
        } else {
            list
        }
        submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDemandeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, missionsMap, onDetailClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    class ViewHolder(
        private val binding: ItemDemandeBinding,
        private val missionsMap: Map<String, Mission>,
        private val onDetailClickListener: ((Mission) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(demande: DemandeParticipation) {
            val mission = missionsMap[demande.missionId]

            binding.tvMissionTitle.text = mission?.titre ?: "Chargement..."
            binding.tvRole.text = demande.roleMission.name
            binding.tvStatus.text = demande.statut.name

            val statusColor = when (demande.statut) {
                DemandeStatus.EN_ATTENTE -> Color.parseColor("#52C2C7")
                DemandeStatus.ACCEPTEE -> Color.parseColor("#4CAF50")
                DemandeStatus.REFUSEE -> Color.parseColor("#F44336")
                DemandeStatus.PRESENT -> Color.parseColor("#2E7D32")
                DemandeStatus.ABSENT -> Color.parseColor("#B71C1C")
            }

            binding.tvStatus.setBackgroundColor(statusColor)
            binding.tvStatus.setTextColor(Color.WHITE)

            // CORRECTION : On laisse le bouton cliquable si la mission existe
            binding.btnVoirDetail.setOnClickListener {
                mission?.let { m ->
                    onDetailClickListener?.invoke(m)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DemandeParticipation>() {
        override fun areItemsTheSame(oldItem: DemandeParticipation, newItem: DemandeParticipation) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DemandeParticipation, newItem: DemandeParticipation) =
            oldItem == newItem
    }
}
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
import com.example.healthproject.data.model.MissionStatus
import com.example.healthproject.databinding.ItemDemandeBinding

class MesDemandesAdapter(
    private val missionsMap: Map<String, Mission> = emptyMap(), // map missionId -> Mission
) : ListAdapter<DemandeParticipation, MesDemandesAdapter.ViewHolder>(DiffCallback()) {

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
        return ViewHolder(binding, missionsMap)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    class ViewHolder(
        private val binding: ItemDemandeBinding,
        private val missionsMap: Map<String, Mission>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(demande: DemandeParticipation) {
            val mission = missionsMap[demande.missionId]

            // Titre de la mission
            binding.tvMissionTitle.text = mission?.titre ?: "Titre non disponible"

            // Rôle
            binding.tvRole.text = demande.roleMission.name

            // Statut
            binding.tvStatus.text = demande.statut.name
            val statusColor = when (demande.statut) {
                DemandeStatus.EN_ATTENTE -> Color.parseColor("#52C2C7") // bleu
                DemandeStatus.ACCEPTEE -> Color.parseColor("#4CAF50")  // vert
                DemandeStatus.REFUSEE -> Color.parseColor("#F44336")   // rouge

                DemandeStatus.PRESENT -> Color.parseColor("#2E7D32")  // vert foncé
                DemandeStatus.ABSENT -> Color.parseColor("#B71C1C")   // rouge foncé
            }

            binding.tvStatus.setBackgroundColor(statusColor)
            binding.tvStatus.setTextColor(Color.WHITE)

            // Optionnel : désactiver interaction si la mission est annulée, cloturée ou en cours
            val canParticipate = mission?.statut == MissionStatus.OUVERTE
            binding.root.isEnabled = canParticipate
            binding.root.alpha = if (canParticipate) 1.0f else 0.5f
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DemandeParticipation>() {
        override fun areItemsTheSame(oldItem: DemandeParticipation, newItem: DemandeParticipation) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DemandeParticipation, newItem: DemandeParticipation) =
            oldItem == newItem
    }
}

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
import com.google.firebase.firestore.FirebaseFirestore

class MesDemandesAdapter :
    ListAdapter<DemandeParticipation, MesDemandesAdapter.ViewHolder>(DiffCallback()) {

    var onDetailClickListener: ((String, Boolean) -> Unit)? = null
    private var filteredList: List<DemandeParticipation> = emptyList()
    private val db = FirebaseFirestore.getInstance()

    fun submitListWithFilter(
        list: List<DemandeParticipation>,
        statusFilter: DemandeStatus? = null
    ) {
        filteredList = if (statusFilter != null) {
            list.filter { it.statut == statusFilter }
        } else {
            list
        }
        submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDemandeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onDetailClickListener, db)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    class ViewHolder(
        private val binding: ItemDemandeBinding,
        private val onDetailClickListener: ((String, Boolean) -> Unit)?,
        private val db: FirebaseFirestore
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(demande: DemandeParticipation) {
            // ✅ Gestion du status
            val statusColor = when (demande.statut) {
                DemandeStatus.EN_ATTENTE -> Color.parseColor("#52C2C7")
                DemandeStatus.ACCEPTEE -> Color.parseColor("#4CAF50")
                DemandeStatus.REFUSEE -> Color.parseColor("#F44336")
                DemandeStatus.PRESENT -> Color.parseColor("#2E7D32")
                DemandeStatus.ABSENT -> Color.parseColor("#B71C1C")
            }

            binding.tvStatus.text = demande.statut.name
            binding.tvStatus.setBackgroundColor(statusColor)
            binding.tvStatus.setTextColor(Color.WHITE)
            binding.tvRole.text = demande.roleMission.name

            // ✅ Récupérer la mission depuis Firestore
            demande.missionId?.let { missionId ->
                db.collection("missions")
                    .document(missionId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val mission = document.toObject(Mission::class.java)
                            binding.tvMissionTitle.text = mission?.titre ?: "Mission inconnue"
                            // tu peux aussi remplir d'autres champs ici
                        } else {
                            binding.tvMissionTitle.text = "Mission inconnue"
                        }
                    }
                    .addOnFailureListener {
                        binding.tvMissionTitle.text = "Erreur de chargement"
                    }
            } ?: run {
                binding.tvMissionTitle.text = "Mission inconnue"
            }

            // ✅ Bouton voir détails
            binding.btnVoirDetail.setOnClickListener {
                val isSuperviseur =
                    demande.statut == DemandeStatus.ACCEPTEE &&
                            demande.roleMission.name.uppercase() == "SUPERVISEUR"

                demande.missionId?.let { id ->
                    onDetailClickListener?.invoke(id, isSuperviseur)
                }
            }
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

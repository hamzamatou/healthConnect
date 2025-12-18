package com.example.healthproject.ui.coordinateur.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.databinding.ItemRequestParticipantBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.healthproject.data.model.User
class RequestsAdapter(
    private val onStatusChanged: (() -> Unit)? = null
) : RecyclerView.Adapter<RequestsAdapter.RequestViewHolder>() {

    private var requests: List<DemandeParticipation> = listOf()
    private val repository = DemandeParticipationRepository()

    fun setRequests(list: List<DemandeParticipation>) {
        requests = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemRequestParticipantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    inner class RequestViewHolder(private val binding: ItemRequestParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(request: DemandeParticipation) {
            binding.textViewNom.text = "Chargement…"
            binding.textViewRole.text = "Rôle: ${request.roleMission}"

            // Charger le nom du user depuis Firestore
            if (!request.userId.isNullOrEmpty()) {
                FirebaseFirestore.getInstance().collection("users")
                    .document(request.userId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject(User::class.java)
                        binding.textViewNom.text =
                            user?.let { "${it.nom} ${it.prenom}" } ?: "Utilisateur inconnu"
                    }
                    .addOnFailureListener {
                        binding.textViewNom.text = "Erreur de chargement"
                    }
            } else {
                binding.textViewNom.text = "Utilisateur inconnu"
            }

            // Confirmer la demande
            binding.btnConfirm.setOnClickListener {
                request.id?.let { id ->
                    repository.updateDemandeStatus(id, DemandeStatus.ACCEPTEE) { success, _ ->
                        if (success) {
                            Toast.makeText(binding.root.context, "Demande confirmée", Toast.LENGTH_SHORT).show()
                            removeRequestFromList(adapterPosition)
                            onStatusChanged?.invoke()
                        }
                    }
                }
            }

            // Annuler la demande
            binding.btnCancel.setOnClickListener {
                request.id?.let { id ->
                    repository.updateDemandeStatus(id, DemandeStatus.REFUSEE) { success, _ ->
                        if (success) {
                            Toast.makeText(binding.root.context, "Demande annulée", Toast.LENGTH_SHORT).show()
                            removeRequestFromList(adapterPosition)
                            onStatusChanged?.invoke()
                        }
                    }
                }
            }
        }
    }

    private fun removeRequestFromList(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val mutableList = requests.toMutableList()
            mutableList.removeAt(position)
            requests = mutableList
            notifyItemRemoved(position)
        }
    }
}

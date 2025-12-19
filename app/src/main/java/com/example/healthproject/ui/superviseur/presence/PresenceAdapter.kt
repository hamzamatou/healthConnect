package com.example.healthproject.ui.superviseur.presence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.data.model.DemandeParticipation
import com.example.healthproject.viewmodel.SuperviseurViewModel

class PresenceAdapter(
    private val viewModel: SuperviseurViewModel
) : RecyclerView.Adapter<PresenceAdapter.ViewHolder>() {

    private val items = mutableListOf<DemandeParticipation>()

    fun submitList(list: List<DemandeParticipation>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRole: TextView = view.findViewById(R.id.txtRole)
        val btnPresent: ImageButton = view.findViewById(R.id.btnPresent)
        val btnAbsent: ImageButton = view.findViewById(R.id.btnAbsent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant_presence, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val demande = items[position]

        holder.txtRole.text = demande.roleMission.name

        holder.btnPresent.setOnClickListener {
            viewModel.markPresence(demande.id!!, true)
        }

        holder.btnAbsent.setOnClickListener {
            viewModel.markPresence(demande.id!!, false)
        }
    }
}

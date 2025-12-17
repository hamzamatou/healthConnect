package com.example.healthproject.ui.coordinateur.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.data.model.Mission
import com.example.healthproject.databinding.ItemMissionBinding
import com.example.healthproject.ui.coordinateur.MissionDetailActivity

class MissionAdapter : RecyclerView.Adapter<MissionAdapter.MissionViewHolder>() {

    private var missions: List<Mission> = listOf()

    fun setMissions(list: List<Mission>) {
        missions = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val binding = ItemMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(missions[position])
    }

    override fun getItemCount(): Int = missions.size

    inner class MissionViewHolder(private val binding: ItemMissionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mission: Mission) {
            binding.textViewMissionTitre.text = mission.titre
            binding.textViewMissionDescription.text = mission.description
            binding.textViewMissionDate.text = "Du ${mission.dateDebut} au ${mission.dateFin}"

            binding.btnViewDetails.setOnClickListener {
                val intent = Intent(binding.root.context, MissionDetailActivity::class.java)
                intent.putExtra("MISSION_ID", mission.id)
                binding.root.context.startActivity(intent)
            }
        }
    }
}

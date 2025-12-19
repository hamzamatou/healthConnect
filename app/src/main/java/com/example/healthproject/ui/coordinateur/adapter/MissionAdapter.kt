package com.example.healthproject.ui.coordinateur.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController
import com.example.healthproject.data.model.Mission
import com.example.healthproject.data.model.UserType
import com.example.healthproject.databinding.ItemMissionBinding
import com.example.healthproject.ui.coordinateur.MissionDetailActivity
import com.example.healthproject.ui.coordinateur.MissionListFragmentDirections
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
class MissionAdapter(private val userType: UserType) :
    RecyclerView.Adapter<MissionAdapter.MissionViewHolder>() {

    private var originalMissions: List<Mission> = listOf()
    private var missions: List<Mission> = listOf()

    fun setMissions(list: List<Mission>) {
        originalMissions = list
        missions = list
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        missions = if (query.isEmpty()) {
            originalMissions
        } else {
            originalMissions.filter { mission ->
                mission.titre.contains(query, ignoreCase = true) ||
                        mission.lieu.contains(query, ignoreCase = true) ||
                        mission.dateDebut.toString().contains(query)
            }
        }
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
            mission.imageBase64?.let {
                try {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.imageViewMission.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    binding.imageViewMission.setImageBitmap(null) // pas d'image si erreur
                }
            } ?: run {
                binding.imageViewMission.setImageBitmap(null) // pas d'image si null
            }

            binding.btnViewDetails.setOnClickListener {
                if (userType == UserType.COORDINATEUR) {
                    // Coordinateur → Intent vers Activity
                    val intent = Intent(binding.root.context, MissionDetailActivity::class.java)
                    intent.putExtra("MISSION_ID", mission.id)
                    binding.root.context.startActivity(intent)
                } else {
                    // Participant → Navigation via NavGraph avec Safe Args
                    val action = MissionListFragmentDirections
                        .actionMissionFragmentToMissionDetailsFragment(missionId = mission.id ?: "")
                    binding.root.findNavController().navigate(action)
                }
            }
        }
    }
}

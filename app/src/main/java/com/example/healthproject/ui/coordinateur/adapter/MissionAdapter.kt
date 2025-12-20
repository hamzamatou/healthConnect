package com.example.healthproject.ui.coordinateur.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.data.model.Mission
import com.example.healthproject.data.model.UserType
import com.example.healthproject.databinding.ItemMissionBinding
import com.example.healthproject.ui.coordinateur.MissionDetailActivity
import android.graphics.BitmapFactory
import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Locale

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
        val binding = ItemMissionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(missions[position])
    }

    override fun getItemCount(): Int = missions.size

    inner class MissionViewHolder(private val binding: ItemMissionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mission: Mission) {
            var nbrtotal=(mission.nbrVolontaire+mission.nbrMedecin+mission.nbrInfirmier).toString()
            binding.textViewMissionTitre.text = mission.titre
            binding.textViewMissionPart.text = nbrtotal
            binding.textViewMissionLoc.text =mission.lieu
            mission.dateDebut?.let { date ->
                // On formate pour obtenir le jour (ex: 12) et le mois (ex: OCT.)
                val sdfDay = SimpleDateFormat("dd", Locale.FRANCE)
                val sdfMonth = SimpleDateFormat("MMM", Locale.FRANCE)
                binding.textViewDateDay.text = sdfDay.format(date)
                binding.textViewDateMonth.text = sdfMonth.format(date).uppercase().replace(".", "")
            }
            mission.imageBase64?.let {
                try {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.imageViewMission.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    binding.imageViewMission.setImageBitmap(null)
                }
            } ?: run {
                binding.imageViewMission.setImageBitmap(null)
            }

            binding.btnViewDetails.setOnClickListener {
                if (userType == UserType.COORDINATEUR) {

                    // ✅ Coordinateur → Activity
                    val intent = Intent(
                        binding.root.context,
                        MissionDetailActivity::class.java
                    )
                    intent.putExtra("MISSION_ID", mission.id)
                    binding.root.context.startActivity(intent)

                } else {

                    // ✅ Participant → Fragment (NavController)
                    val bundle = Bundle().apply {
                        putString("missionId", mission.id)
                    }

                    binding.root.findNavController().navigate(
                        R.id.missionDetailsFragment,
                        bundle
                    )
                }
            }
        }
    }
}

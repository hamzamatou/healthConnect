package com.example.healthproject.ui.coordinateur.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.healthproject.databinding.DialogCreateMissionBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreateMissionDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogCreateMissionBinding
    private val db = FirebaseFirestore.getInstance() // <-- Instance Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCreateMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateMission.setOnClickListener {
            val missionName = binding.editTextMissionName.text.toString()
            if (missionName.isNotEmpty()) {
                // TODO: Ajouter la mission
                Toast.makeText(context, "Mission créée: $missionName", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

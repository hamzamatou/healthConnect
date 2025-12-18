package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.data.model.Mission
import com.example.healthproject.data.model.UserType
import com.example.healthproject.databinding.FragmentMissionListBinding
import com.example.healthproject.ui.coordinateur.adapter.MissionAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MissionListFragment : Fragment() {

    private var _binding: FragmentMissionListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MissionAdapter
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    // 🔹 Récupérer le type de l'utilisateur actuel (exemple)
    private val currentUserType: UserType
        get() {
            // Ici, tu peux récupérer depuis ton ViewModel ou Firebase
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            // Exemple simple : hardcodé pour test
            return if (uid == "coordinateurId") UserType.COORDINATEUR else UserType.PARTICIPANT
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 Adapter avec rôle utilisateur
        adapter = MissionAdapter(currentUserType)
        binding.recyclerViewMissions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMissions.adapter = adapter

        // 🔹 Charger les missions depuis Firestore en temps réel
        listenerRegistration = db.collection("missions")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Erreur lors de la récupération
                    return@addSnapshotListener
                }

                val missions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Mission::class.java)?.copy(id = doc.id)
                } ?: listOf()

                adapter.setMissions(missions)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove() // Supprimer l'écouteur pour éviter les fuites mémoire
        _binding = null
    }
}

package com.example.healthproject.ui.coordinateur

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthproject.data.model.DemandeStatus
import com.example.healthproject.data.model.email.* // Assurez-vous d'avoir ces modèles
import com.example.healthproject.data.repository.DemandeParticipationRepository
import com.example.healthproject.databinding.ActivityAllRequestsBinding
import com.example.healthproject.ui.coordinateur.adapter.RequestsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AllRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllRequestsBinding
    private val repository = DemandeParticipationRepository()
    private lateinit var adapter: RequestsAdapter
    private var missionTitle: String = "votre mission" // Variable pour stocker le titre

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val missionId = intent.getStringExtra("MISSION_ID") ?: return

        // Charger le titre de la mission pour l'utiliser dans l'email
        fetchMissionTitle(missionId)

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadRequests(missionId)
    }

    private fun fetchMissionTitle(missionId: String) {
        FirebaseFirestore.getInstance().collection("missions").document(missionId)
            .get()
            .addOnSuccessListener { doc ->
                missionTitle = doc.getString("titre") ?: "Mission"
            }
    }

    private fun setupRecyclerView() {
        adapter = RequestsAdapter(onStatusChanged = { userId, isAccepted ->
            if (isAccepted) {
                sendAcceptanceEmail(userId, missionTitle)
            }
            // Pas besoin de recharger toute la liste car l'adapter gère le remove local
        })
        binding.recyclerViewRequests.apply {
            layoutManager = LinearLayoutManager(this@AllRequestsActivity)
            adapter = this@AllRequestsActivity.adapter
        }
    }

    private fun loadRequests(missionId: String) {
        repository.getDemandesByMissionAndStatus(missionId, DemandeStatus.EN_ATTENTE) { list ->
            adapter.setRequests(list)
        }
    }

    private fun sendAcceptanceEmail(userId: String, title: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.brevo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(BrevoApi::class.java)
        val apiKey = "key"

        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { uDoc ->
                val email = uDoc.getString("email") ?: return@addOnSuccessListener
                val name = uDoc.getString("nom") ?: "Participant"

                val body = EmailBody(
                    sender = Sender("HealthApp", "nourkalay21@gmail.com"),
                    to = listOf(Recipient(email)),
                    subject = "Acceptation : $title",
                    textContent = "Bonjour $name, votre demande pour la mission « $title » a été acceptée."
                )

                service.sendEmail(body, apiKey).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) Log.d("Email", "Envoyé à $email")
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Email", "Erreur réseau", t)
                    }
                })
            }
    }
}
package com.example.healthproject.ui.participant

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.data.model.Message
import com.example.healthproject.databinding.ItemMessageBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val currentUserId: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages = listOf<Message>()
    private val db = FirebaseFirestore.getInstance() // instance Firestore

    fun submitList(list: List<Message>) {
        messages = list
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding =
            ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        // Afficher le nom de l'expéditeur
        if (message.senderId == currentUserId) {
            holder.binding.textViewSenderName.text = "Vous"
        } else {
            // Récupérer le nom depuis Firestore
            db.collection("participants") // Nom de ta collection d'utilisateurs
                .document(message.senderId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nom = document.getString("nom") ?: ""
                        val prenom = document.getString("prenom") ?: ""
                        holder.binding.textViewSenderName.text = "$nom $prenom"
                    } else {
                        holder.binding.textViewSenderName.text = "Utilisateur inconnu"
                    }
                }
                .addOnFailureListener {
                    holder.binding.textViewSenderName.text = "Utilisateur inconnu"
                }
        }

        // Afficher le contenu du message
        holder.binding.textViewMessage.text = message.contenu

        // Afficher l'heure
        val date = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(message.timestamp))
        holder.binding.textViewDate.text = date

        // Aligner le message à gauche ou à droite selon l'expéditeur
        val params = holder.binding.bubbleLayout.layoutParams as LinearLayout.LayoutParams
        if (message.senderId == currentUserId) {
            params.gravity = Gravity.END
            holder.binding.bubbleLayout.setBackgroundResource(R.drawable.bubble_sender)
        } else {
            params.gravity = Gravity.START
            holder.binding.bubbleLayout.setBackgroundResource(R.drawable.bubble_receiver)
        }
        holder.binding.bubbleLayout.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size
}

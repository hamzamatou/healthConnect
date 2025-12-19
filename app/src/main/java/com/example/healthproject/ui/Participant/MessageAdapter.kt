package com.example.healthproject.ui.participant

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.data.model.Message
import com.example.healthproject.databinding.ItemMessageBinding
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val currentUserId: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages = listOf<Message>()

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

        holder.binding.textViewMessage.text = message.contenu

        val date = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(message.timestamp))
        holder.binding.textViewDate.text = date

        val params = holder.binding.bubbleLayout.layoutParams as LinearLayout.LayoutParams

        if (message.senderId == currentUserId) {
            params.gravity = Gravity.END
            holder.binding.bubbleLayout
                .setBackgroundResource(R.drawable.bubble_sender)
        } else {
            params.gravity = Gravity.START
            holder.binding.bubbleLayout
                .setBackgroundResource(R.drawable.bubble_receiver)
        }

        holder.binding.bubbleLayout.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size
}

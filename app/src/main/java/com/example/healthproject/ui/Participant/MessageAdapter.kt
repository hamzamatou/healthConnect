package com.example.healthproject.ui.Participant
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.data.model.Message
import com.example.healthproject.databinding.ItemMessageBinding

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
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.textViewMessage.text = message.contenu

        val params = holder.binding.messageContainer.layoutParams as LinearLayout.LayoutParams
        if (message.senderId == currentUserId) {
            params.gravity = Gravity.END
            //holder.binding.textViewMessage.setBackgroundResource(R.drawable.bubble_sender)
        } else {
            params.gravity = Gravity.START
            //holder.binding.textViewMessage.setBackgroundResource(R.drawable.bubble_receiver)
        }
        holder.binding.messageContainer.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size
}

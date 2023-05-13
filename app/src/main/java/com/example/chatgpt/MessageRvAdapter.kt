package com.example.chatgpt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageRvAdapter(private val messageList: ArrayList<MessageRvModal>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_message_rv_item, parent, false)
            UserMessageViewHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.bot_message_rv_item, parent, false)
            BotMessageViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sender = messageList.get(position).sender
        when (sender) {
            "user" -> (holder as UserMessageViewHolder).userMsgTv.text =
                messageList.get(position).messege
            "bot" -> (holder as BotMessageViewHolder).botMsgTv.text =
                messageList.get(position).messege
        }
    }

    inner class UserMessageViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val userMsgTv: TextView = itemView.findViewById(R.id.userTv)
    }

    inner class BotMessageViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val botMsgTv: TextView = itemView.findViewById(R.id.botTv)
    }

    override fun getItemViewType(position: Int): Int {
        return when (messageList.get(position).sender) {
            "user" -> 0
            "bot" -> 1
            else -> 1
        }
    }
}

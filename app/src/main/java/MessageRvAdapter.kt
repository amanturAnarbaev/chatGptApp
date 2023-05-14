package com.example.chatgpt

import android.os.Handler
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
        val sender = messageList[position].sender
        when (sender) {
            "user" -> (holder as UserMessageViewHolder).userMsgTv.text =
                messageList[position].message
            "bot" -> {
                val isLastMessage = position == messageList.lastIndex
                (holder as BotMessageViewHolder).bind(messageList[position].message, isLastMessage)
            }
        }
    }

    inner class UserMessageViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val userMsgTv: TextView = itemView.findViewById(R.id.userTv)
    }

    inner class BotMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val botMsgTv: TextView = itemView.findViewById(R.id.botTv)
        private val handler = Handler()
        private val delayMillis = 50L // Change this value to adjust the typing speed

        fun bind(message: String, isLastMessage: Boolean) {
            if (isLastMessage) {
                val typingText = "Typing..."
                botMsgTv.text = typingText

                // Stop any existing animation
                handler.removeCallbacksAndMessages(null)

                // Set the text to an empty string
                botMsgTv.text = ""

                // Start the typing animation
                val chars = message.toCharArray()
                var i = 0
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        if (i <= chars.size) {
                            botMsgTv.text = message.substring(0, i)
                            i++
                            handler.postDelayed(this, delayMillis)
                        }
                    }
                }, delayMillis)
            } else {
                botMsgTv.text = message
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (messageList.get(position).sender) {
            "user" -> 0
            "bot" -> 1
            else -> 1
        }
    }
}
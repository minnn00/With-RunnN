package com.with_runn.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.with_runn.R

class ChatMessageAdapter : ListAdapter<ChatMessage, ChatMessageAdapter.MessageViewHolder>(
    MessageDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_MY_MESSAGE -> R.layout.item_message_my
            VIEW_TYPE_COURSE_SHARE -> R.layout.item_course_share
            else -> R.layout.item_message_other
        }
        
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return when {
            message.isCourseShare -> VIEW_TYPE_COURSE_SHARE
            message.isFromMe -> VIEW_TYPE_MY_MESSAGE
            else -> VIEW_TYPE_OTHER_MESSAGE
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView? = itemView.findViewById(R.id.message_text)
        private val timestamp: TextView? = itemView.findViewById(R.id.timestamp)
        private val profileImage: ImageView? = itemView.findViewById(R.id.profile_image)

        fun bind(message: ChatMessage) {
            // 메시지 텍스트는 일반 메시지에만 있음
            messageText?.text = message.content
            
            // 시간은 일반 메시지에만 있음
            timestamp?.text = message.timestamp
            
            // 프로필 이미지는 상대방 메시지에만 있음
            profileImage?.setImageResource(R.drawable.jonny)
        }
    }

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
        private const val VIEW_TYPE_COURSE_SHARE = 3
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.content == newItem.content && oldItem.timestamp == newItem.timestamp
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
} 
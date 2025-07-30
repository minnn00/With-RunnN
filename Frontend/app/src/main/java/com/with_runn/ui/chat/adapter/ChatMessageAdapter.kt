package com.with_runn.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.with_runn.R
import com.with_runn.ui.chat.model.Message

class ChatMessageAdapter : ListAdapter<Message, ChatMessageAdapter.MessageViewHolder>(
    MessageDiffCallback()
) {
    
    private var onProfileImageClickListener: ((String) -> Unit)? = null
    
    fun setOnProfileImageClickListener(listener: (String) -> Unit) {
        onProfileImageClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_MY_MESSAGE -> R.layout.item_message_my
            VIEW_TYPE_COURSE_SHARE -> R.layout.item_course_share
            VIEW_TYPE_SYSTEM_MESSAGE -> R.layout.item_system_message
            else -> R.layout.item_message_other
        }
        
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position), onProfileImageClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return when {
            message.isSystemMessage -> VIEW_TYPE_SYSTEM_MESSAGE
            message.isCourseShare -> VIEW_TYPE_COURSE_SHARE
            message.isFromMe -> VIEW_TYPE_MY_MESSAGE
            else -> VIEW_TYPE_OTHER_MESSAGE
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView? = itemView.findViewById(R.id.message_text)
        private val timestamp: TextView? = itemView.findViewById(R.id.timestamp)
        private val profileImage: ImageView? = itemView.findViewById(R.id.profile_image)
        private val systemMessageText: TextView? = itemView.findViewById(R.id.system_message_text)

        fun bind(message: Message, onProfileImageClickListener: ((String) -> Unit)?) {
            when {
                message.isSystemMessage -> {
                    // 시스템 메시지 처리
                    systemMessageText?.text = message.content
                }
                else -> {
                    // 일반 메시지 처리
                    messageText?.text = message.content
                    timestamp?.text = message.timestamp
                    profileImage?.setImageResource(R.drawable.jonny)
                    // 프로필 이미지 클릭 리스너 설정
                    profileImage?.setOnClickListener {
                        onProfileImageClickListener?.invoke(message.sender)
                    }
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
        private const val VIEW_TYPE_COURSE_SHARE = 3
        private const val VIEW_TYPE_SYSTEM_MESSAGE = 4
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
} 
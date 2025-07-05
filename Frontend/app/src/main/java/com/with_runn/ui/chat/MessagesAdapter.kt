package com.with_runn.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.Message
import com.with_runn.data.MessageType
import com.with_runn.databinding.ItemChatDateSeparatorBinding
import com.with_runn.databinding.ItemChatReceivedBinding
import com.with_runn.databinding.ItemChatSentBinding
import com.with_runn.databinding.ItemChatSystemMessageBinding

class MessagesAdapter(
    private val onProfileClickListener: OnProfileClickListener? = null
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback) {

    interface OnProfileClickListener {
        fun onProfileClicked(userId: String)
    }

    companion object {
        const val VIEW_TYPE_SENT = 0
        const val VIEW_TYPE_RECEIVED = 1
        const val VIEW_TYPE_DATE_SEPARATOR = 2
        const val VIEW_TYPE_SYSTEM_MESSAGE = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            MessageType.SENT -> VIEW_TYPE_SENT
            MessageType.RECEIVED -> VIEW_TYPE_RECEIVED
            MessageType.DATE_SEPARATOR -> VIEW_TYPE_DATE_SEPARATOR
            MessageType.SYSTEM_MESSAGE -> VIEW_TYPE_SYSTEM_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val binding = ItemChatSentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SentMessageViewHolder(binding)
            }
            VIEW_TYPE_RECEIVED -> {
                val binding = ItemChatReceivedBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ReceivedMessageViewHolder(binding, onProfileClickListener)
            }
            VIEW_TYPE_DATE_SEPARATOR -> {
                val binding = ItemChatDateSeparatorBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                DateSeparatorViewHolder(binding)
            }
            VIEW_TYPE_SYSTEM_MESSAGE -> {
                val binding = ItemChatSystemMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SystemMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
            is DateSeparatorViewHolder -> holder.bind(message)
            is SystemMessageViewHolder -> holder.bind(message)
        }
    }

    // 내가 보낸 메시지 ViewHolder
    class SentMessageViewHolder(
        private val binding: ItemChatSentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.apply {
                tvMessage.text = message.content
                tvMessageTime.text = message.timeString
            }
        }
    }

    // 받은 메시지 ViewHolder
    class ReceivedMessageViewHolder(
        private val binding: ItemChatReceivedBinding,
        private val onProfileClickListener: OnProfileClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.apply {
                tvMessage.text = message.content
                tvMessageTime.text = message.timeString
                tvSenderName.text = message.senderName

                // 프로필 이미지 로드
                message.senderProfileUrl?.let { url ->
                    Glide.with(itemView.context)
                        .load(url)
                        .placeholder(R.drawable.img_app_logo)
                        .error(R.drawable.img_app_logo)
                        .into(ivSenderProfile)
                } ?: run {
                    ivSenderProfile.setImageResource(R.drawable.img_app_logo)
                }

                // 프로필 이미지 클릭 리스너 설정
                ivSenderProfile.setOnClickListener {
                    message.senderId?.let { userId ->
                        onProfileClickListener?.onProfileClicked(userId)
                    }
                }
            }
        }
    }

    // 날짜 구분선 ViewHolder
    class DateSeparatorViewHolder(
        private val binding: ItemChatDateSeparatorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.tvDate.text = message.dateString
        }
    }

    // 시스템 메시지 ViewHolder
    class SystemMessageViewHolder(
        private val binding: ItemChatSystemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.tvSystemMessage.text = message.content
        }
    }

    object MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
} 
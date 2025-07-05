package com.with_runn.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.ChatRoom
import com.with_runn.databinding.ItemChatListBinding

class ChatListAdapter(
    private val onItemClick: (ChatRoom) -> Unit,
    private val onItemSwipe: (ChatRoom) -> Unit
) : ListAdapter<ChatRoom, ChatListAdapter.ChatRoomViewHolder>(ChatRoomDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val binding = ItemChatListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatRoomViewHolder(
        private val binding: ItemChatListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(chatRoom: ChatRoom) {
            binding.apply {
                // 프로필 이미지
                Glide.with(itemView.context)
                    .load(chatRoom.profileImageUrl)
                    .placeholder(R.drawable.img_app_logo)
                    .error(R.drawable.img_app_logo)
                    .into(ivProfileImage)

                // 채팅방 이름
                tvChatRoomName.text = chatRoom.name

                // 마지막 메시지
                tvLastMessage.text = chatRoom.lastMessage

                // 마지막 메시지 시간
                tvLastMessageTime.text = chatRoom.lastMessageTime

                // 읽지 않은 메시지 수
                if (chatRoom.unreadCount > 0) {
                    tvUnreadCount.visibility = View.VISIBLE
                    tvUnreadCount.text = when {
                        chatRoom.unreadCount > 99 -> "99+"
                        else -> chatRoom.unreadCount.toString()
                    }
                } else {
                    tvUnreadCount.visibility = View.GONE
                }
            }
        }
    }

    object ChatRoomDiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }
    }
} 
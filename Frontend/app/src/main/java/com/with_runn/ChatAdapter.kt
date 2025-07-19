package com.with_runn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    
    private var chatRooms: List<ChatRoom> = listOf()
    private var onItemClickListener: ((ChatRoom) -> Unit)? = null
    
    fun updateChatRooms(newChatRooms: List<ChatRoom>) {
        chatRooms = newChatRooms
        notifyDataSetChanged()
    }
    
    fun setOnItemClickListener(listener: (ChatRoom) -> Unit) {
        onItemClickListener = listener
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatRooms[position])
    }
    
    override fun getItemCount(): Int = chatRooms.size
    
    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        private val profileImage2: ImageView = itemView.findViewById(R.id.profile_image_2)
        private val chatName: TextView = itemView.findViewById(R.id.chat_name)
        private val chatTime: TextView = itemView.findViewById(R.id.chat_time)
        private val lastMessage: TextView = itemView.findViewById(R.id.last_message)
        private val notificationBadge: View = itemView.findViewById(R.id.notification_badge)
        private val notificationCount: TextView = itemView.findViewById(R.id.notification_count)
        
        fun bind(chatRoom: ChatRoom) {
            chatName.text = chatRoom.name
            chatTime.text = chatRoom.time
            lastMessage.text = chatRoom.lastMessage
            
            // 프로필 이미지 설정
            if (chatRoom.profileImageResId != 0) {
                profileImage.setImageResource(chatRoom.profileImageResId)
            }
            
            // 두 번째 프로필 이미지 설정 (겹친 이미지)
            if (chatRoom.hasSecondImage && chatRoom.profileImage2ResId != 0) {
                profileImage2.visibility = View.VISIBLE
                profileImage2.setImageResource(chatRoom.profileImage2ResId)
            } else {
                profileImage2.visibility = View.GONE
            }
            
            // 알림 배지 설정
            if (chatRoom.notificationCount > 0) {
                notificationBadge.visibility = View.VISIBLE
                notificationCount.visibility = View.VISIBLE
                notificationCount.text = chatRoom.notificationCount.toString()
            } else {
                notificationBadge.visibility = View.GONE
                notificationCount.visibility = View.GONE
            }
            
            // 클릭 리스너 설정
            itemView.setOnClickListener {
                onItemClickListener?.invoke(chatRoom)
            }
        }
    }
} 
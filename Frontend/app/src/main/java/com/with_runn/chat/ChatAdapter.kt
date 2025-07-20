package com.with_runn.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import com.with_runn.R

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    
    private var chatRooms: List<ChatRoom> = listOf()
    private var onItemClickListener: ((ChatRoom) -> Unit)? = null
    private var onDeleteClickListener: ((ChatRoom, Int) -> Unit)? = null
    private var swipedPosition = -1
    
    fun updateChatRooms(newChatRooms: List<ChatRoom>) {
        chatRooms = newChatRooms
        notifyDataSetChanged()
    }
    
    fun setOnItemClickListener(listener: (ChatRoom) -> Unit) {
        onItemClickListener = listener
    }
    
    fun setOnDeleteClickListener(listener: (ChatRoom, Int) -> Unit) {
        onDeleteClickListener = listener
    }
    
    fun showDeleteButton(position: Int) {
        val previousSwipedPosition = swipedPosition
        swipedPosition = position
        
        if (previousSwipedPosition != -1 && previousSwipedPosition != position) {
            notifyItemChanged(previousSwipedPosition)
        }
        notifyItemChanged(position)
    }
    
    fun hideDeleteButton() {
        if (swipedPosition != -1) {
            val position = swipedPosition
            swipedPosition = -1
            notifyItemChanged(position)
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatRooms[position], position)
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
        private val deleteButton: TextView = itemView.findViewById(R.id.delete_button)
        private val deleteBackground: View = itemView.findViewById(R.id.delete_background)
        private val chatItemContainer: ConstraintLayout = itemView.findViewById(R.id.chat_item_container)
        
        fun bind(chatRoom: ChatRoom, position: Int) {
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
            
            // 스와이프 상태에 따라 삭제 버튼 표시/숨김
            if (position == swipedPosition) {
                deleteButton.visibility = View.VISIBLE
                deleteBackground.visibility = View.VISIBLE
            } else {
                deleteButton.visibility = View.GONE
                deleteBackground.visibility = View.GONE
            }
            
            // 클릭 리스너 설정
            chatItemContainer.setOnClickListener {
                onItemClickListener?.invoke(chatRoom)
            }
            
            // 삭제 버튼 클릭 리스너
            deleteButton.setOnClickListener {
                onDeleteClickListener?.invoke(chatRoom, position)
            }
        }
        
        fun getChatRoom(): ChatRoom? {
            return if (adapterPosition != RecyclerView.NO_POSITION) {
                chatRooms[adapterPosition]
            } else null
        }
    }
} 
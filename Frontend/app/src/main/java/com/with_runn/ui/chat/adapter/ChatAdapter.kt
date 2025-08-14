package com.with_runn.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.ui.chat.model.ChatRoom

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
        val layoutRes = when (viewType) {
            VIEW_TYPE_GROUP_CHAT -> R.layout.item_chat_group
            else -> R.layout.item_chat
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutRes, parent, false)
        return ChatViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatRooms[position], position)
    }
    
    override fun getItemCount(): Int = chatRooms.size
    
    override fun getItemViewType(position: Int): Int {
        return if (chatRooms[position].hasSecondImage) {
            VIEW_TYPE_GROUP_CHAT
        } else {
            VIEW_TYPE_SINGLE_CHAT
        }
    }
    
    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView? = itemView.findViewById(R.id.profile_image)
        private val profileImage2: ImageView? = itemView.findViewById(R.id.profile_image_2)
        private val chatName: TextView? = itemView.findViewById(R.id.chat_name)
        private val chatTime: TextView? = itemView.findViewById(R.id.chat_time)
        private val lastMessage: TextView? = itemView.findViewById(R.id.last_message)
        private val notificationBadge: View? = itemView.findViewById(R.id.notification_badge)
        private val notificationCount: TextView? = itemView.findViewById(R.id.notification_count)
        private val participantCount: TextView? = itemView.findViewById(R.id.participant_count)
        private val deleteButton: TextView? = itemView.findViewById(R.id.delete_button)
        private val deleteBackground: View? = itemView.findViewById(R.id.delete_background)
        private val deleteContainer: FrameLayout? = itemView.findViewById(R.id.delete_container)
        private val chatItemContainer: ConstraintLayout? = itemView.findViewById(R.id.chat_item_container)
        
        fun bind(chatRoom: ChatRoom, position: Int) {
            chatName?.text = chatRoom.name
            chatTime?.text = chatRoom.time
            lastMessage?.text = chatRoom.lastMessage
            
            // 프로필 이미지 설정 (S3 URL에서 로딩)
            profileImage?.let { imageView ->
                if (!chatRoom.profileImageUrl.isNullOrEmpty()) {
                    // URL 디코딩 후 재인코딩
                    val decodedUrl = java.net.URLDecoder.decode(chatRoom.profileImageUrl, "UTF-8")
                    val encodedUrl = java.net.URLEncoder.encode(decodedUrl, "UTF-8")
                    
                    // 디버깅 로그
                    android.util.Log.d("ChatAdapter", "원본 URL: ${chatRoom.profileImageUrl}")
                    android.util.Log.d("ChatAdapter", "디코딩된 URL: $decodedUrl")
                    android.util.Log.d("ChatAdapter", "재인코딩된 URL: $encodedUrl")
                    
                    // S3 Access Denied 에러 처리
                    android.util.Log.w("ChatAdapter", "S3 Access Denied 에러 발생 - 백엔드 팀에 S3 권한 설정 요청 필요")
                    
                    // S3 URL에서 이미지 로딩
                    Glide.with(itemView.context)
                        .load(decodedUrl)
                        .placeholder(R.drawable.img_profile_default)
                        .error(R.drawable.img_profile_default)
                        .into(imageView)
                } else {
                    // URL이 없으면 기본 이미지
                    imageView.setImageResource(R.drawable.img_profile_default)
                }
            }
            
            // 두 번째 프로필 이미지 설정 (겹친 이미지)
            if (chatRoom.hasSecondImage) {
                profileImage2?.visibility = View.VISIBLE
                profileImage2?.let { imageView ->
                    if (!chatRoom.profileImage2Url.isNullOrEmpty()) {
                        // URL 디코딩 후 재인코딩
                        val decodedUrl = java.net.URLDecoder.decode(chatRoom.profileImage2Url, "UTF-8")
                        val encodedUrl = java.net.URLEncoder.encode(decodedUrl, "UTF-8")
                        
                        // S3 URL에서 이미지 로딩
                        Glide.with(itemView.context)
                            .load(decodedUrl)
                            .placeholder(R.drawable.img_profile_default)
                            .error(R.drawable.img_profile_default)
                            .into(imageView)
                    } else {
                        // URL이 없으면 기본 이미지
                        imageView.setImageResource(R.drawable.img_profile_default)
                    }
                }
            } else {
                profileImage2?.visibility = View.GONE
            }
            
            // 참여자 수 설정 (그룹 채팅인 경우)
            participantCount?.text = chatRoom.participants.toString()
            
            // 알림 배지 설정
            if (chatRoom.notificationCount > 0) {
                notificationBadge?.visibility = View.VISIBLE
                notificationCount?.visibility = View.VISIBLE
                notificationCount?.text = chatRoom.notificationCount.toString()
            } else {
                notificationBadge?.visibility = View.GONE
                notificationCount?.visibility = View.GONE
            }
            
            // 스와이프 상태에 따라 삭제 버튼 표시/숨김
            if (position == swipedPosition) {
                deleteContainer?.visibility = View.VISIBLE
                deleteContainer?.translationX = 0f
                chatItemContainer?.translationX = -80f * itemView.resources.displayMetrics.density
            } else {
                deleteContainer?.visibility = View.GONE
                deleteContainer?.translationX = 0f
                chatItemContainer?.translationX = 0f
            }
            
            // 클릭 리스너 설정
            chatItemContainer?.setOnClickListener {
                onItemClickListener?.invoke(chatRoom)
            }
            
            // 삭제 버튼 클릭 리스너
            deleteContainer?.setOnClickListener {
                onDeleteClickListener?.invoke(chatRoom, position)
            }
        }
        
        fun getChatRoom(): ChatRoom? {
            return if (adapterPosition != RecyclerView.NO_POSITION) {
                chatRooms[adapterPosition]
            } else null
        }
    }
    
    companion object {
        private const val VIEW_TYPE_SINGLE_CHAT = 1
        private const val VIEW_TYPE_GROUP_CHAT = 2
    }
} 
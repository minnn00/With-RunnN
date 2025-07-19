package com.with_runn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendsAdapter : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {
    
    private var friends = listOf<Friend>()
    private var onItemClickListener: ((Friend) -> Unit)? = null
    
    fun updateFriends(newFriends: List<Friend>) {
        friends = newFriends
        notifyDataSetChanged()
    }
    
    fun setOnItemClickListener(listener: (Friend) -> Unit) {
        onItemClickListener = listener
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_card, parent, false)
        return FriendViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
        
        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(friend)
        }
    }
    
    override fun getItemCount(): Int = friends.size
    
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        private val dogName: TextView = itemView.findViewById(R.id.dog_name)
        private val personalityTag: TextView = itemView.findViewById(R.id.personality_tag)
        private val dogImage: ImageView = itemView.findViewById(R.id.dog_image)
        
        fun bind(friend: Friend) {
            dogName.text = friend.name
            personalityTag.text = friend.personalityTag
            
            // 강아지 이미지 설정
            dogImage.setImageResource(friend.imageResId)
            
            // 성격 태그들 설정 (첫 번째와 두 번째 태그만 표시)
            if (friend.personalityTags.isNotEmpty()) {
                val firstTag = itemView.findViewById<TextView>(R.id.first_personality_tag)
                val secondTag = itemView.findViewById<TextView>(R.id.second_personality_tag)
                
                if (firstTag != null && friend.personalityTags.isNotEmpty()) {
                    firstTag.text = friend.personalityTags[0]
                }
                
                if (secondTag != null && friend.personalityTags.size > 1) {
                    secondTag.text = friend.personalityTags[1]
                }
            }
        }
    }
} 
package com.with_runn.ui.friend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.ui.friend.model.dto.RecommendedFriendResponse

class RecommendedFriendAdapter(
    private var friends: List<RecommendedFriendResponse> = emptyList(),
    private val onItemClick: ((RecommendedFriendResponse) -> Unit)? = null
) : RecyclerView.Adapter<RecommendedFriendAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dogImage: ImageView = view.findViewById(R.id.dog_image)
        val dogName: TextView = view.findViewById(R.id.dog_name)
        val personalityTag: TextView = view.findViewById(R.id.personality_tag)
        val firstPersonalityTag: TextView = view.findViewById(R.id.first_personality_tag)
        val secondPersonalityTag: TextView = view.findViewById(R.id.second_personality_tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]

        // 강아지 이름 설정 (null 안전성 추가)
        holder.dogName.text = friend.userName ?: ""

        // 프로필 이미지 설정 (null 안전성 추가)
        val profileImageUrl = friend.profileImage
        if (!profileImageUrl.isNullOrEmpty() && profileImageUrl != "example.url") {
            Glide.with(holder.dogImage.context)
                .load(profileImageUrl)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .centerCrop()
                .into(holder.dogImage)
        } else {
            holder.dogImage.setImageResource(R.drawable.default_profile)
        }

        // 성격 태그 설정 (null 안전성 추가)
        val styleText = friend.style?.firstOrNull() ?: ""
        holder.personalityTag.text = "#$styleText"

        // 성격 태그들 설정 (첫 번째와 두 번째 태그만 표시)
        val allTags = (friend.style ?: emptyList()) + (friend.characters ?: emptyList())
        
        if (allTags.isNotEmpty()) {
            holder.firstPersonalityTag.text = "#${allTags[0]}"
            holder.firstPersonalityTag.visibility = View.VISIBLE
            
            if (allTags.size > 1) {
                holder.secondPersonalityTag.text = "#${allTags[1]}"
                holder.secondPersonalityTag.visibility = View.VISIBLE
            } else {
                holder.secondPersonalityTag.visibility = View.GONE
            }
        } else {
            holder.firstPersonalityTag.visibility = View.GONE
            holder.secondPersonalityTag.visibility = View.GONE
        }

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(friend)
        }
    }

    override fun getItemCount() = friends.size

    fun updateFriends(newFriends: List<RecommendedFriendResponse>) {
        friends = newFriends
        notifyDataSetChanged()
    }
} 
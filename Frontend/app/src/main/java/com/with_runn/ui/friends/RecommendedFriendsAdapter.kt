package com.with_runn.ui.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.Friend
import com.with_runn.databinding.ItemRecommendedFriendBinding

class RecommendedFriendsAdapter(
    private val friends: List<Friend>,
    private val onItemClick: (Friend) -> Unit
) : RecyclerView.Adapter<RecommendedFriendsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendedFriendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
    }

    override fun getItemCount(): Int = friends.size

    inner class ViewHolder(
        private val binding: ItemRecommendedFriendBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: Friend) {
            // Glide를 사용하여 이미지 로딩 최적화
            Glide.with(binding.root.context)
                .load(R.drawable.img_profile_johnny)
                .centerCrop()
                .dontAnimate()
                .into(binding.ivDogProfile)

            // 클릭 리스너 설정
            binding.root.setOnClickListener {
                onItemClick(friend)
            }
        }
    }
} 
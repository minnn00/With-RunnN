package com.with_runn.ui.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.chip.Chip
import com.with_runn.R
import com.with_runn.data.Friend
import com.with_runn.databinding.ItemFriendCardBinding

class FriendsAdapter(
    private val onItemClick: (Friend) -> Unit
) : ListAdapter<Friend, FriendsAdapter.FriendViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = getItem(position)
        holder.bind(friend)
    }

    inner class FriendViewHolder(
        private val binding: ItemFriendCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: Friend) {
            binding.apply {
                // 이미지 로드
                Glide.with(itemView.context)
                    .load(friend.imageUrl)
                    .transform(RoundedCorners(24))
                    .placeholder(R.drawable.img_app_logo)
                    .error(R.drawable.img_app_logo)
                    .into(ivDogImage)

                // 이름 설정
                tvDogName.text = friend.name

                // 태그 설정
                setupTags(friend.tags)

                // 클릭 리스너
                root.setOnClickListener {
                    onItemClick(friend)
                }
            }
        }

        private fun setupTags(tags: List<String>) {
            binding.chipGroupTags.removeAllViews()
            
            tags.forEach { tag ->
                val chip = Chip(itemView.context).apply {
                    text = if (tag.startsWith("#")) tag else "#$tag"
                    textSize = 10f
                    setTextColor(itemView.context.getColor(R.color.gray_700))
                    chipBackgroundColor = itemView.context.getColorStateList(R.color.gray_100)
                    chipCornerRadius = itemView.context.resources.getDimension(R.dimen.chip_corner_radius)
                    chipStrokeWidth = 0f
                    isCheckable = false
                    isClickable = false
                }
                binding.chipGroupTags.addView(chip)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
} 
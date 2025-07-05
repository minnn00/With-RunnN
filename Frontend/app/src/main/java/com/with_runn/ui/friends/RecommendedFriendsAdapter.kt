package com.with_runn.ui.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.with_runn.R
import com.with_runn.data.Friend
import com.with_runn.databinding.ItemRecommendedCardBinding

class RecommendedFriendsAdapter(
    private val friends: List<Friend>,
    private val onCardClick: (Friend) -> Unit
) : RecyclerView.Adapter<RecommendedFriendsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRecommendedCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.apply {
                tvDogName.text = friend.name
                tvDogAge.text = friend.age
                tvBreed.text = friend.breed
                tvLocation.text = friend.category
                
                // 프로필 이미지 설정 (임시로 기본 이미지 사용)
                // TODO: 실제 이미지 URL이 있을 때 Glide나 Picasso로 로드
                ivDogImage.setImageResource(R.drawable.ic_friends)
                
                // 태그 설정 - ChipGroup에 동적으로 추가
                chipGroupTags.removeAllViews()
                friend.tags.forEach { tag ->
                    val chip = Chip(binding.root.context)
                    chip.text = "#$tag"
                    chip.textSize = 12f
                    chip.setTextColor(binding.root.context.getColor(R.color.green_700))
                    chip.chipBackgroundColor = binding.root.context.getColorStateList(R.color.white)
                    chip.chipCornerRadius = binding.root.context.resources.getDimension(R.dimen.chip_corner_radius)
                    chip.chipMinHeight = binding.root.context.resources.getDimension(R.dimen.chip_corner_radius) + 16f
                    chip.chipStrokeWidth = 0f
                    chipGroupTags.addView(chip)
                }
                
                // 카드 클릭 리스너
                root.setOnClickListener {
                    onCardClick(friend)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendedCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friends[position])
    }

    override fun getItemCount(): Int = friends.size
} 
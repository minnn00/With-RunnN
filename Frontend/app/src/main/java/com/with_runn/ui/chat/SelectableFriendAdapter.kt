package com.with_runn.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.Friend
import com.with_runn.databinding.ItemSelectableFriendBinding

class SelectableFriendAdapter(
    private val onSelectionChanged: (Friend, Boolean) -> Unit
) : ListAdapter<Friend, SelectableFriendAdapter.SelectableFriendViewHolder>(FriendDiffCallback) {

    private val selectedItems = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableFriendViewHolder {
        val binding = ItemSelectableFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectableFriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectableFriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SelectableFriendViewHolder(
        private val binding: ItemSelectableFriendBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: Friend) {
            binding.apply {
                // 프로필 이미지
                Glide.with(itemView.context)
                    .load(friend.imageUrl)
                    .placeholder(R.drawable.img_app_logo)
                    .error(R.drawable.img_app_logo)
                    .into(ivProfile)

                // 이름
                tvName.text = friend.name

                // 선택 상태
                val isSelected = selectedItems.contains(friend.id)
                cvCheckMark.visibility = if (isSelected) View.VISIBLE else View.GONE

                // 클릭 리스너
                root.setOnClickListener {
                    toggleSelection(friend)
                }
            }
        }

        private fun toggleSelection(friend: Friend) {
            val isCurrentlySelected = selectedItems.contains(friend.id)
            
            if (isCurrentlySelected) {
                selectedItems.remove(friend.id)
                binding.cvCheckMark.visibility = View.GONE
                onSelectionChanged(friend, false)
            } else {
                selectedItems.add(friend.id)
                binding.cvCheckMark.visibility = View.VISIBLE
                onSelectionChanged(friend, true)
            }
        }
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItems(): Set<String> {
        return selectedItems.toSet()
    }

    object FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
} 
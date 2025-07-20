package com.with_runn.ui.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R

class FriendAddAdapter : ListAdapter<Friend, FriendAddAdapter.FriendViewHolder>(FriendDiffCallback()) {

    private val selectedFriends = mutableSetOf<Friend>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_add, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedFriends(): List<Friend> = selectedFriends.toList()

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.friend_profile_image)
        private val nameText: TextView = itemView.findViewById(R.id.friend_name)
        private val checkbox: CheckBox = itemView.findViewById(R.id.friend_checkbox)

        fun bind(friend: Friend) {
            nameText.text = friend.name
            profileImage.setImageResource(friend.imageResId)
            
            checkbox.isChecked = selectedFriends.contains(friend)
            
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedFriends.add(friend)
                } else {
                    selectedFriends.remove(friend)
                }
            }
        }
    }

    private class FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
}

 
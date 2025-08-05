package com.with_runn.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.model.Follower
import com.with_runn.databinding.ItemMypageFollowerProfileBinding

class MypageFollowersRecyclerViewAdapter(private var list: List<Follower>) : RecyclerView.Adapter<MypageFollowersRecyclerViewAdapter.FollowerViewHolder>() {
    inner class FollowerViewHolder(private val binding: ItemMypageFollowerProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Follower) {
            binding.nameText.text = item.name
            Glide.with(binding.userImg.context)
                .load(item.profileImage)
                .into(binding.userImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerViewHolder {
        val binding = ItemMypageFollowerProfileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FollowerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowerViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun updateFollowerData(newList: List<Follower>) {
        list = newList
        notifyDataSetChanged()
    }
    fun updateFollowingData(newList: List<Follower>) {
        list = newList
        notifyDataSetChanged()
    }
}

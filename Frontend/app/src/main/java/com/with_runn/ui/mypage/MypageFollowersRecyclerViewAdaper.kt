package com.with_runn.ui.mypage

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.model.Follower
import com.with_runn.data.viewmodel.MypageFollowerViewmodel
import com.with_runn.databinding.DialogFriendProfileBinding
import com.with_runn.databinding.ItemMypageFollowerProfileBinding
import com.with_runn.ui.chat.activity.ChatActivity
import com.with_runn.ui.friend.FriendProfileDialogFragment

class MypageFollowersRecyclerViewAdapter(
    private var list: List<Follower>,
    private val fragmentManager: androidx.fragment.app.FragmentManager
) : RecyclerView.Adapter<MypageFollowersRecyclerViewAdapter.FollowerViewHolder>() {

    inner class FollowerViewHolder(private val binding: ItemMypageFollowerProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Follower) {
            binding.nameText.text = item.name
            Glide.with(binding.userImg.context)
                .load(item.profileImage)
                .placeholder(R.drawable.default_profile)
                .into(binding.userImg)

            binding.profileLayout.setOnClickListener {
                MypageUserProfileDialogFragment
                    .newInstance(item.targetUserId)
                    .show(fragmentManager, "FriendProfileDialog")
            }

            binding.followBtn.setOnClickListener {
                // TODO: follow API 호출
            }
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

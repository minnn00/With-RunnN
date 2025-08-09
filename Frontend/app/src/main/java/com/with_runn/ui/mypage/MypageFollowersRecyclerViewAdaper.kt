package com.with_runn.ui.mypage

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.model.Follower
import com.with_runn.databinding.DialogFriendProfileBinding
import com.with_runn.databinding.ItemMypageFollowerProfileBinding

class MypageFollowersRecyclerViewAdapter(private var list: List<Follower>) : RecyclerView.Adapter<MypageFollowersRecyclerViewAdapter.FollowerViewHolder>() {
    inner class FollowerViewHolder(private val binding: ItemMypageFollowerProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Follower) {
            binding.nameText.text = item.name
            Glide.with(binding.userImg.context)
                .load(item.profileImage)
                .into(binding.userImg)

            binding.profileLayout.setOnClickListener {
                //todo: 프로필 dialog 로직
                showFollowerDialog(binding.root.context, item.targetUserId)
            }
            binding.followBtn.setOnClickListener {
                //todo: 팔로우 로직
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

    private fun showFollowerDialog(context: Context, followerId: Int) {
        val dialogBinding = DialogFriendProfileBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()
        dialog.show()

        // 1️⃣ API 호출 (예: Retrofit)
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = ApiClient.api.getUserProfile(followerId) // 가상의 API
//                if (response.isSuccessful) {
//                    val data = response.body()
//                    withContext(Dispatchers.Main) {
//                        data?.let {
//                            Glide.with(context).load(it.profileImage).into(imgView)
//                            nameView.text = it.name
//                            introView.text = it.introduction ?: "소개글이 없습니다."
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        // 2️⃣ 버튼 이벤트 자리
//        followBtn.setOnClickListener {
//            // TODO: 팔로우 API 호출 로직
//        }
//
//        messageBtn.setOnClickListener {
//            // TODO: 메시지 화면 이동 로직
//        }
    }
}

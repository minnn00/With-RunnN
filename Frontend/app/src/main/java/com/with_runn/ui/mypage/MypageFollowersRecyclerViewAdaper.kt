package com.with_runn.ui.mypage

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.model.Follower
import com.with_runn.data.viewmodel.MypageFollowerViewmodel
import com.with_runn.databinding.DialogFriendProfileBinding
import com.with_runn.databinding.ItemMypageFollowerProfileBinding
import com.with_runn.ui.chat.activity.ChatActivity

class MypageFollowersRecyclerViewAdapter(
    private var list: List<Follower>,
    private val viewModel: MypageFollowerViewmodel,
    private val lifecycleOwner: LifecycleOwner
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
                showFollowerDialog(binding.root.context, item.targetUserId)
            }
            binding.followBtn.setOnClickListener {
//                viewModel.followUser(item.targetUserId)
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

        // 다이얼로그 표시
        dialog.show()


        // 프로필 정보 API 호출
        viewModel.loadFriendDetail(followerId)

        // LiveData 옵저빙
        viewModel.friendDetail.observe(lifecycleOwner) { detail ->
            dialogBinding.dogName.text = detail.name
            dialogBinding.dogIntro.text = detail.introduction
            Glide.with(context)
                .load(detail.profileImage ?: R.drawable.default_profile)
                .into(dialogBinding.profileImage)
        }

        viewModel.error.observe(lifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        // 버튼 이벤트
        dialogBinding.followButton.setOnClickListener {
//            viewModel.followUser(followerId)
        }

        dialogBinding.messageButton.setOnClickListener {
//            val intent = Intent(context, ChatActivity::class.java)
//            intent.putExtra("targetUserId", followerId)
//            context.startActivity(intent)
//            dialog.dismiss()
        }
    }
}

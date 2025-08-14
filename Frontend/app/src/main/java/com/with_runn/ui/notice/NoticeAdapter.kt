package com.with_runn.ui.notice

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R
import com.with_runn.data.model.Notice
import com.with_runn.data.viewmodel.MypageFollowerViewmodel
import com.with_runn.databinding.ItemNoticeFollowBinding
import com.with_runn.databinding.ItemNoticeLikeBinding
import com.with_runn.databinding.ItemNoticeScrapBinding
import com.with_runn.ui.mypage.MypageUserProfileDialogFragment

class NoticeAdapter(
    private var noticeList: List<Notice>,
    private val fragmentManager: androidx.fragment.app.FragmentManager,
    private val viewModel: MypageFollowerViewmodel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LIKE = 0
        private const val TYPE_FOLLOW = 1
        private const val TYPE_SCRAP = 2
    }

    override fun getItemViewType(position: Int): Int = when (noticeList[position].noticeType) {
        "LIKE" -> TYPE_LIKE
        "FOLLOW" -> TYPE_FOLLOW
        "SCRAP" -> TYPE_SCRAP
        else -> TYPE_LIKE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LIKE -> LikeViewHolder(ItemNoticeLikeBinding.inflate(inflater, parent, false))
            TYPE_FOLLOW -> FollowViewHolder(ItemNoticeFollowBinding.inflate(inflater, parent, false))
            TYPE_SCRAP -> ScrapViewHolder(ItemNoticeScrapBinding.inflate(inflater, parent, false))
            else -> LikeViewHolder(ItemNoticeLikeBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notice = noticeList[position]
        when (holder) {
            is LikeViewHolder -> holder.bind(notice)
            is FollowViewHolder -> holder.bind(notice)
            is ScrapViewHolder -> holder.bind(notice)
        }
    }

    override fun getItemCount(): Int = noticeList.size

    fun updateData(newList: List<Notice>) {
        noticeList = newList
        notifyDataSetChanged()
    }

    // --------------------------
    // ViewHolder
    // --------------------------

    inner class LikeViewHolder(private val binding: ItemNoticeLikeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notice: Notice) {
            binding.noticeText.text = notice.message

            // 예: 클릭 기능 연결
            binding.root.setOnClickListener {
                handleLikeClick(notice)
            }

            binding.profileLayout.setOnClickListener {
                MypageUserProfileDialogFragment
                    .newInstance(notice.actorId)
                    .show(fragmentManager, "FriendProfileDialog")
            }
            binding.followBtn.setOnClickListener {
                viewModel.followUser(notice.actorId)
                binding.followBtn.background = AppCompatResources.getDrawable(itemView.context,R.drawable.bg_button_inactive)
                binding.followBtn.text = "팔로잉"
                binding.followBtn.setTextColor(itemView.context.getColor(R.color.green_700))
                Log.d("followBtn", "followBtn clicked")
            }
        }

        private fun handleLikeClick(notice: Notice) {
            // 이미 구현된 좋아요 기능 호출
        }
    }

    inner class FollowViewHolder(private val binding: ItemNoticeFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notice: Notice) {
            binding.noticeText.text = notice.message

            // 팔로우 버튼 클릭
            binding.followBtn.setOnClickListener {
                handleFollowClick(notice)
            }

            binding.profileLayout.setOnClickListener {
                MypageUserProfileDialogFragment
                    .newInstance(notice.actorId)
                    .show(fragmentManager, "FriendProfileDialog")
            }
            binding.followBtn.setOnClickListener {
                viewModel.followUser(notice.actorId)
                binding.followBtn.background = AppCompatResources.getDrawable(itemView.context,R.drawable.bg_button_inactive)
                binding.followBtn.text = "팔로잉"
                binding.followBtn.setTextColor(itemView.context.getColor(R.color.green_700))
                Log.d("followBtn", "followBtn clicked")
            }
        }

        private fun handleFollowClick(notice: Notice) {
            // 이미 구현된 팔로우 기능 호출
        }
    }

    inner class ScrapViewHolder(private val binding: ItemNoticeScrapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notice: Notice) {
            binding.noticeText.text = notice.message

            // 예: 스크랩 아이템 클릭
            binding.root.setOnClickListener {
                handleScrapClick(notice)
            }

            binding.profileLayout.setOnClickListener {
                MypageUserProfileDialogFragment
                    .newInstance(notice.actorId)
                    .show(fragmentManager, "FriendProfileDialog")
            }
            binding.followBtn.setOnClickListener {
                viewModel.followUser(notice.actorId)
                binding.followBtn.background = AppCompatResources.getDrawable(itemView.context,R.drawable.bg_button_inactive)
                binding.followBtn.text = "팔로잉"
                binding.followBtn.setTextColor(itemView.context.getColor(R.color.green_700))
                Log.d("followBtn", "followBtn clicked")
            }
        }

        private fun handleScrapClick(notice: Notice) {
            // 이미 구현된 스크랩 기능 호출
        }
    }
}



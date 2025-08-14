package com.with_runn.ui.mypage

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.viewmodel.MypageFollowerViewmodel
import com.with_runn.databinding.DialogFriendProfileBinding
import com.with_runn.ui.friend.BlockUserDialogFragment
import com.with_runn.ui.friend.ReportActivity
import kotlin.math.log

class MypageUserProfileDialogFragment : DialogFragment() {

    private var _binding: DialogFriendProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MypageFollowerViewmodel by viewModels({ requireParentFragment() })

    private var isFollowing = false
    private var followerId: Int = -1

    private var friendName: String? = null
    private var userId: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFriendProfileBinding.inflate(LayoutInflater.from(context))
        followerId = arguments?.getInt(ARG_FOLLOWER_ID) ?: -1

        if (followerId != -1) {
            setupObservers()
            viewModel.loadFriendDetail(followerId)
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupObservers() {
        viewModel.friendDetail.observe(this) { detail ->

            friendName = detail.name
            userId = detail.userId

            binding.dogName.text = friendName
            binding.dogIntro.text = detail.introduction
            Glide.with(requireContext())
                .load(detail.profileImage ?: R.drawable.default_profile)
                .circleCrop()
                .into(binding.profileImage)

            isFollowing = false // 서버 응답 필드에 맞게 수정
            updateFollowButtonUI()
        }

        viewModel.followResult.observe(this) { success ->
            if (success) {
                isFollowing = true
                updateFollowButtonUI()
            }
        }

        viewModel.error.observe(this) { errorMsg ->

            errorMsg?.let {
                when {
                    it.contains("이미 팔로우한 사용자") -> {
                        // 이미 팔로우된 상태로 UI 업데이트
                        updateFollowButtonState(true)
                        Toast.makeText(requireContext(), "이미 팔로우한 사용자입니다", Toast.LENGTH_SHORT).show()
                        // 팔로우 버튼 비활성화 유지
                    }
                    it.contains("서버 내부 오류") || it.contains("응답을 읽을 수 없습니다") -> {
                        // 서버 오류 시 사용자 친화적 메시지 표시
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // 기타 에러 처리
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.followButton.setOnClickListener {
            if (!isFollowing) {
                viewModel.followUser(followerId)
            }
        }

        binding.messageButton.setOnClickListener {
            // 메시지 화면 이동 로직
        }

        binding.closeButton.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        binding.menuButton.setOnClickListener {
            showMenuPopup(it)
        }
    }

    private fun updateFollowButtonUI() {
        if (isFollowing) {
            binding.followButton.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_button_inactive)
            binding.followButton.text = "팔로잉"
            binding.followButton.setTextColor(requireContext().getColor(R.color.green_700))
        } else {
            binding.followButton.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_button_main)
            binding.followButton.text = "팔로우"
            binding.followButton.setTextColor(requireContext().getColor(R.color.white))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_FOLLOWER_ID = "follower_id"

        fun newInstance(followerId: Int): MypageUserProfileDialogFragment {
            val fragment = MypageUserProfileDialogFragment()
            val args = Bundle()
            args.putInt(ARG_FOLLOWER_ID, followerId)
            fragment.arguments = args
            return fragment
        }
    }

    private fun updateFollowButtonState(isFollowing: Boolean) {
        val followButton = view?.findViewById<TextView>(R.id.follow_button)
        followButton?.let {
            if (isFollowing) {
                it.text = "팔로잉"
                it.setBackgroundResource(R.drawable.bg_button_inactive)
                it.setTextColor(resources.getColor(R.color.gray, null))
            } else {
                it.text = "팔로우"
                it.setBackgroundResource(R.drawable.bg_button_main)
                it.setTextColor(resources.getColor(R.color.white, null))
            }
        }
    }

    private fun showMenuPopup(anchorView: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_menu, null)

        // 팝업 뷰의 크기를 측정
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val popupWindow = PopupWindow(
            popupView,
            popupView.measuredWidth,
            popupView.measuredHeight
        )

        // 팝업 윈도우 설정
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 10f

        // 팝업 표시 (메뉴 버튼 아래, 더 오른쪽으로)
        popupWindow.showAsDropDown(anchorView, -popupWindow.width + anchorView.width + 80, 0)

        // 팝업 메뉴 아이템 클릭 리스너
        popupView.findViewById<View>(R.id.block_button)?.setOnClickListener {
            // 차단하기 다이얼로그 표시
            popupWindow.dismiss()
            val blockDialog = BlockUserDialogFragment.newInstance(friendName!!, userId!!)
            blockDialog.show(childFragmentManager, "BlockUserDialog")
        }

        popupView.findViewById<View>(R.id.report_button)?.setOnClickListener {
            // 신고하기 Activity 실행
            popupWindow.dismiss()
            val intent = Intent(requireContext(), ReportActivity::class.java)
            startActivity(intent)
        }
    }
}

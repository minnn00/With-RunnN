package com.with_runn.ui.friends

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.Friend
import com.with_runn.data.Gender
import com.with_runn.databinding.DialogFriendDetailBinding
import com.with_runn.ui.report.ReportUserActivity

class FriendDetailDialogFragment : DialogFragment() {

    private var _binding: DialogFriendDetailBinding? = null
    private val binding get() = _binding!!

    private var friend: Friend? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_With_Runn)
        
        // Arguments에서 Friend 객체 받기
        arguments?.let {
            friend = it.getParcelable(ARG_FRIEND)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFriendDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        setupViews()
        setupClickListeners()
    }

    private fun setupDialog() {
        dialog?.window?.let { window ->
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setWindowAnimations(R.style.Theme_With_Runn)
        }
    }

    private fun setupViews() {
        friend?.let { friend ->
            binding.apply {
                // 프로필 이미지
                Glide.with(requireContext())
                    .load(friend.imageUrl)
                    .placeholder(R.drawable.img_app_logo)
                    .error(R.drawable.img_app_logo)
                    .into(ivProfileImage)

                // 이름
                tvProfileName.text = friend.name

                // 성별 아이콘
                when (friend.gender) {
                    Gender.MALE -> {
                        ivGender.setImageResource(R.drawable.ic_male)
                        ivGender.setColorFilter(resources.getColor(R.color.blue_500, null))
                    }
                    Gender.FEMALE -> {
                        ivGender.setImageResource(R.drawable.ic_female)
                        ivGender.setColorFilter(resources.getColor(R.color.red_500, null))
                    }
                }

                // 소개글
                tvProfileBio.text = friend.bio

                // 상세 정보
                tvAge.text = friend.age
                tvBreed.text = friend.breed
                tvCategory.text = friend.category

                // 팔로우 버튼 상태
                updateFollowButton(friend.isFollowing)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // 닫기 버튼
            ivClose.setOnClickListener {
                dismiss()
            }

            // 케밥 메뉴 버튼
            ivKebabMenu.setOnClickListener { view ->
                showKebabMenu(view)
            }

            // 팔로우 버튼
            btnFollow.setOnClickListener {
                friend?.let { currentFriend ->
                    val newFollowState = !currentFriend.isFollowing
                    updateFollowButton(newFollowState)
                    
                    // 실제 구현에서는 서버에 팔로우 상태 업데이트
                    Log.d("FriendDetail", "팔로우 상태 변경: ${currentFriend.name} -> $newFollowState")
                }
            }

            // 메시지 버튼
            btnMessage.setOnClickListener {
                friend?.let { currentFriend ->
                    Log.d("FriendDetail", "메시지 보내기: ${currentFriend.name}")
                    // 실제 구현에서는 채팅 화면으로 이동
                    // 예: startActivity(Intent(context, ChatRoomActivity::class.java).apply {
                    //     putExtra("friend_id", currentFriend.id)
                    // })
                }
            }
        }
    }

    private fun updateFollowButton(isFollowing: Boolean) {
        binding.btnFollow.apply {
            if (isFollowing) {
                text = "팔로잉"
                setBackgroundColor(resources.getColor(R.color.gray_400, null))
            } else {
                text = "팔로우"
                setBackgroundColor(resources.getColor(R.color.green_700, null))
            }
        }
    }

    private fun showKebabMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_friend_detail, popupMenu.menu)
        
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_block -> {
                    showBlockDialog()
                    true
                }
                R.id.action_report -> {
                    startReportActivity()
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }

    private fun showBlockDialog() {
        friend?.let { currentFriend ->
            AlertDialog.Builder(requireContext())
                .setTitle("차단하기")
                .setMessage("${currentFriend.name}님을 차단하시겠습니까?")
                .setPositiveButton("차단") { _, _ ->
                    // 차단 로직 구현
                    Log.d("FriendDetail", "차단하기: ${currentFriend.name}")
                    showBlockConfirmationDialog(currentFriend.name)
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    private fun showBlockConfirmationDialog(friendName: String) {
        AlertDialog.Builder(requireContext())
            .setMessage("차단하였습니다.")
            .setPositiveButton("닫기") { _, _ ->
                dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun startReportActivity() {
        friend?.let { currentFriend ->
            val intent = Intent(requireContext(), ReportUserActivity::class.java).apply {
                putExtra("friend_id", currentFriend.id)
                putExtra("friend_name", currentFriend.name)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_FRIEND = "friend"
        
        fun newInstance(friend: Friend): FriendDetailDialogFragment {
            return FriendDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_FRIEND, friend)
                }
            }
        }
    }
} 
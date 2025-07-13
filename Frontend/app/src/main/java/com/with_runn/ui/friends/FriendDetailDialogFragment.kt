package com.with_runn.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.with_runn.R
import com.with_runn.data.Friend
import com.with_runn.data.Gender
import com.with_runn.databinding.DialogFriendDetailBinding

class FriendDetailDialogFragment : DialogFragment() {

    private var _binding: DialogFriendDetailBinding? = null
    private val binding get() = _binding!!

    private var friend: Friend? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_With_Runn)
        
        arguments?.let {
            friend = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_FRIEND, Friend::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(ARG_FRIEND)
            }
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
        }
    }

    private fun setupViews() {
        friend?.let { friend ->
            binding.apply {
                // 프로필 이미지 (모든 프로필을 조니 이미지로 통일)
                ivProfileImage.setImageResource(R.drawable.img_profile_johnny)

                // 이름
                tvName.text = friend.name

                // 성별 아이콘
                when (friend.gender) {
                    Gender.MALE -> {
                        ivGender.setImageResource(R.drawable.ic_male)
                        ivGender.setColorFilter(resources.getColor(R.color.green_700, null))
                    }
                    Gender.FEMALE -> {
                        ivGender.setImageResource(R.drawable.ic_female)
                        ivGender.setColorFilter(resources.getColor(R.color.green_700, null))
                    }
                }

                // 소개
                tvIntro.text = friend.bio

                // 정보 표 (나이, 견종, 분류)
                // infoTable의 각 자식 TextView에 직접 값 할당
                // 0: "나이", 1: 실제 나이, 2: "견종", 3: 실제 견종, 4: "분류", 5: 실제 분류
                val infoTable = infoTable
                if (infoTable.childCount >= 6) {
                    // 나이
                    (infoTable.getChildAt(1) as? android.widget.TextView)?.text = friend.age
                    // 견종
                    (infoTable.getChildAt(3) as? android.widget.TextView)?.text = friend.breed
                    // 분류
                    (infoTable.getChildAt(5) as? android.widget.TextView)?.text = friend.category
                }

                // 팔로우 버튼 상태
                updateFollowButton(friend.isFollowing)
            }
        }
    }

    private fun setupChipGroup(chipGroup: com.google.android.material.chip.ChipGroup, items: List<String>) {
        chipGroup.removeAllViews()
        items.forEach { item ->
            val chip = Chip(requireContext()).apply {
                text = item
                setTextColor(resources.getColor(R.color.gray_700, null))
                chipBackgroundColor = resources.getColorStateList(R.color.gray_100, null)
                isCheckable = false
                isClickable = false
            }
            chipGroup.addView(chip)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // 팔로우 버튼
            btnFollow.setOnClickListener {
                friend?.let { currentFriend ->
                    val newFollowState = !currentFriend.isFollowing
                    currentFriend.isFollowing = newFollowState
                    updateFollowButton(newFollowState)
                }
            }

            // 메시지 버튼
            btnMessage.setOnClickListener {
                // TODO: 채팅방으로 이동
                dismiss()
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
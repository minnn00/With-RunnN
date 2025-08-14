package com.with_runn.ui.friend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.ui.friend.viewmodel.RecommendedFriendViewModel
import com.with_runn.data.repository.MypageFollowerRepository
import kotlinx.coroutines.launch

class FriendProfileDialogFragment : DialogFragment() {
    private var friendName: String = ""
    private var userId: Int = 0
    private var onMessageButtonClickListener: (() -> Unit)? = null
    private lateinit var viewModel: RecommendedFriendViewModel
    private val mypageFollowerRepository = MypageFollowerRepository()
    private var isFollowingCurrent: Boolean = false

    companion object {
        private const val ARG_FRIEND_NAME = "friend_name"
        private const val ARG_PERSONALITY_TAG = "personality_tag"
        private const val ARG_PERSONALITY_TAGS = "personality_tags"
        private const val ARG_IMAGE_RES_ID = "image_res_id"
        private const val ARG_USER_ID = "user_id"

        fun newInstance(
            friendName: String,
            personalityTag: String,
            personalityTags: ArrayList<String>,
            imageResId: Int,
            userId: Int
        ): FriendProfileDialogFragment {
            val fragment = FriendProfileDialogFragment()
            val args = Bundle()
            args.putString(ARG_FRIEND_NAME, friendName)
            args.putString(ARG_PERSONALITY_TAG, personalityTag)
            args.putStringArrayList(ARG_PERSONALITY_TAGS, personalityTags)
            args.putInt(ARG_IMAGE_RES_ID, imageResId)
            args.putInt(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_friend_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dialog 설정
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // ViewModel 설정
        viewModel = ViewModelProvider(this)[RecommendedFriendViewModel::class.java]

        // 데이터 가져오기
        friendName = arguments?.getString(ARG_FRIEND_NAME) ?: ""
        val personalityTag = arguments?.getString(ARG_PERSONALITY_TAG) ?: ""
        val personalityTags = arguments?.getStringArrayList(ARG_PERSONALITY_TAGS) ?: arrayListOf()
        val imageResId = arguments?.getInt(ARG_IMAGE_RES_ID, R.drawable.maru) ?: R.drawable.maru
        userId = arguments?.getInt(ARG_USER_ID, 0) ?: 0

        // 초기 UI 설정
        setupInitialUI(friendName, personalityTag, personalityTags, imageResId)

        // 세부 프로필 정보 로드
        if (userId > 0) {
            loadFriendDetail()
            // 마이페이지 팔로잉 목록으로 초기 팔로우 상태 판별 (임시)
            checkInitialFollowingState(userId)
        }

        // Observer 설정
        setupObservers()
    }

    private fun setupInitialUI(
        friendName: String,
        personalityTag: String,
        personalityTags: ArrayList<String>,
        imageResId: Int
    ) {
        // 프로필 이미지 설정
        val profileImage = view?.findViewById<ImageView>(R.id.profile_image)
        profileImage?.setImageResource(imageResId)

        // 강아지 이름 설정
        val nameText = view?.findViewById<TextView>(R.id.dog_name)
        nameText?.text = friendName

        // 한줄 소개 설정
        val introText = view?.findViewById<TextView>(R.id.dog_intro)
        introText?.text = "안녕하세요 ${friendName}입니다요"

        // 닫기 버튼 설정
        val closeButton = view?.findViewById<ImageView>(R.id.close_button)
        closeButton?.setOnClickListener {
            dismiss()
        }

        // 메뉴 버튼 설정
        val menuButton = view?.findViewById<ImageView>(R.id.menu_button)
        menuButton?.setOnClickListener {
            showMenuPopup(it)
        }

        // 팔로우 버튼 설정 - 초기 상태는 팔로우 가능
        val followButton = view?.findViewById<TextView>(R.id.follow_button)
        followButton?.let {
            // 초기 상태 설정
            it.text = "팔로우"
            it.setBackgroundResource(R.drawable.bg_btn_filled)
            it.setTextColor(resources.getColor(R.color.white, null))
            it.isEnabled = true
            it.contentDescription = "팔로우할 수 있는 사용자입니다"
            
            it.setOnClickListener {
                if (userId > 0) {
                    // 팔로우 API 호출
                    viewModel.followFriend(userId)
                    // 버튼은 updateFollowButtonLoadingState()에서 처리됨
                }
            }
        }

        // 메시지 버튼 설정
        val messageButton = view?.findViewById<TextView>(R.id.message_button)
        messageButton?.setOnClickListener {
            // 메시지 버튼 클릭 리스너 호출
            onMessageButtonClickListener?.invoke()
            dismiss() // 다이얼로그 닫기
        }
    }

    private fun loadFriendDetail() {
        viewModel.loadFriendDetail(userId)
    }

    private fun setupObservers() {
        viewModel.friendDetail.observe(viewLifecycleOwner) { friendDetail ->
            friendDetail?.let {
                updateUIWithDetail(it)
            }
        }

        viewModel.followResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                // 팔로우 성공 시 UI 업데이트
                isFollowingCurrent = true
                updateFollowButtonState(true)
                // 성공 메시지 표시
                showFollowSuccessMessage("팔로우 완료 (userId=$userId)")
                viewModel.clearFollowResult()
            }
        }

        viewModel.blockResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                // 차단 성공 시 UI 업데이트
                showBlockSuccessMessage(result)
                viewModel.clearBlockResult()
                // 차단 완료 후 다이얼로그 닫기
                dismiss()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                when {
                    it.contains("이미 팔로우한 사용자") -> {
                        // 이미 팔로우된 상태로 UI 업데이트
                        updateFollowButtonState(true)
                        showFollowSuccessMessage("이미 팔로우한 사용자입니다")
                        // 팔로우 버튼 비활성화 유지
                    }
                    it.contains("서버 내부 오류") || it.contains("응답을 읽을 수 없습니다") -> {
                        // 서버 오류 시 사용자 친화적 메시지 표시
                        showErrorMessage(it)
                        // 에러 발생 시 팔로우 버튼을 에러 상태로 표시 (재시도 가능)
                        updateFollowButtonErrorState()
                    }
                    else -> {
                        // 기타 에러 처리
                        showErrorMessage(it)
                        // 에러 발생 시 팔로우 버튼을 에러 상태로 표시 (재시도 가능)
                        updateFollowButtonErrorState()
                    }
                }
                viewModel.clearError()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // 로딩 상태에 따른 UI 업데이트
            val followButton = view?.findViewById<TextView>(R.id.follow_button)
            if (isLoading) {
                // 로딩 중 상태 표시
                updateFollowButtonLoadingState()
            } else {
                // 로딩 완료 시 현재 팔로우 상태로 복원
                // 로컬에 추적한 현재 상태 사용
                updateFollowButtonState(isFollowingCurrent)
            }
        }
    }

    private fun updateUIWithDetail(friendDetail: com.with_runn.ui.friend.model.dto.FriendDetailResponse) {
        // 프로필 이미지 업데이트
        val profileImage = view?.findViewById<ImageView>(R.id.profile_image)
        if (!friendDetail.profileImage.isNullOrEmpty() && friendDetail.profileImage != "example.url") {
            Glide.with(this)
                .load(friendDetail.profileImage)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .centerCrop()
                .into(profileImage!!)
        }

        // 이름 업데이트
        val nameText = view?.findViewById<TextView>(R.id.dog_name)
        nameText?.text = friendDetail.name ?: friendName

        // 소개 업데이트
        val introText = view?.findViewById<TextView>(R.id.dog_intro)
        introText?.text = friendDetail.introduction ?: "안녕하세요 ${friendDetail.name ?: friendName}입니다요"

        // 추가 정보들 업데이트 (UI에 해당 필드들이 있다면)
        // 예: 품종, 성별, 나이, 크기 등
        updateAdditionalInfo(friendDetail)
    }

    private fun updateAdditionalInfo(friendDetail: com.with_runn.ui.friend.model.dto.FriendDetailResponse) {
        // UI에 추가 정보 필드들이 있다면 여기서 업데이트
        // 예: 품종, 성별, 나이, 크기 등을 표시하는 TextView들
    }

    private fun updateFollowButtonState(isFollowing: Boolean) {
        val followButton = view?.findViewById<TextView>(R.id.follow_button)
        followButton?.let {
            if (isFollowing) {
                // 임시 정책: 팔로우 중이면 버튼 문구를 "이미 팔로우 상태"로 표시
                it.text = "이미 팔로우 상태"
                it.setBackgroundResource(R.drawable.bg_btn_filled_inactive)
                it.setTextColor(resources.getColor(R.color.gray, null))
                it.isEnabled = false
                // 팔로잉 상태임을 명확히 표시
                it.contentDescription = "이미 팔로우한 사용자입니다"
            } else {
                it.text = "팔로우"
                it.setBackgroundResource(R.drawable.bg_btn_filled)
                it.setTextColor(resources.getColor(R.color.white, null))
                it.isEnabled = true
                // 팔로우 가능 상태임을 명확히 표시
                it.contentDescription = "팔로우할 수 있는 사용자입니다"
            }
        }
    }

    /**
     * 마이페이지 팔로잉 목록을 조회하여 해당 사용자를 팔로우 중인지 초기 상태를 판별한다 (임시 구현)
     */
    private fun checkInitialFollowingState(targetUserId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val followings = mypageFollowerRepository.fetchFollowings()
                val isFollowing = followings?.any { it.targetUserId == targetUserId } == true
                if (isFollowing) {
                    updateFollowButtonState(true)
                }
            } catch (_: Exception) {
                // 실패 시 무시하고 기본 상태(팔로우 가능) 유지
            }
        }
    }

    private fun showFollowSuccessMessage(message: String) {
        // 팔로우 성공 메시지 표시
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showBlockSuccessMessage(message: String) {
        // 차단 성공 메시지 표시
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(error: String) {
        // 에러 메시지 표시
        val message = when {
            error.contains("이미 차단된 사용자") -> {
                "이미 차단된 사용자입니다."
            }
            error.contains("서버 내부 오류") -> {
                "서버에 일시적인 문제가 발생했습니다.\n잠시 후 다시 시도해주세요."
            }
            error.contains("응답을 읽을 수 없습니다") -> {
                "서버 응답에 문제가 있습니다.\n잠시 후 다시 시도해주세요."
            }
            error.contains("네트워크") -> {
                "네트워크 연결을 확인해주세요."
            }
            else -> {
                "오류: $error"
            }
        }
        
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }

    /**
     * 팔로우 버튼을 로딩 상태로 표시
     */
    private fun updateFollowButtonLoadingState() {
        val followButton = view?.findViewById<TextView>(R.id.follow_button)
        followButton?.let {
            it.text = "처리 중..."
            it.setBackgroundResource(R.drawable.bg_btn_filled_inactive)
            it.setTextColor(resources.getColor(R.color.gray, null))
            it.isEnabled = false
            it.contentDescription = "팔로우 요청 처리 중입니다"
        }
    }

    /**
     * 팔로우 버튼을 에러 상태로 표시 (재시도 가능)
     */
    private fun updateFollowButtonErrorState() {
        val followButton = view?.findViewById<TextView>(R.id.follow_button)
        followButton?.let {
            it.text = "다시 시도"
            it.setBackgroundResource(R.drawable.bg_btn_filled)
            it.setTextColor(resources.getColor(R.color.white, null))
            it.isEnabled = true
            it.contentDescription = "팔로우 요청에 실패했습니다. 다시 시도할 수 있습니다"
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
            val blockDialog = BlockUserDialogFragment.newInstance(friendName, userId)
            blockDialog.show(childFragmentManager, "BlockUserDialog")
        }
        
        popupView.findViewById<View>(R.id.report_button)?.setOnClickListener {
            // 신고하기 Activity 실행
            popupWindow.dismiss()
            val intent = Intent(requireContext(), ReportActivity::class.java)
            intent.putExtra(ReportActivity.EXTRA_REPORTED_USER_ID, userId)
            intent.putExtra(ReportActivity.EXTRA_REPORTED_USER_NAME, friendName)
            startActivity(intent)
        }
    }
    
    /**
     * 메시지 버튼 클릭 리스너 설정
     */
    fun setOnMessageButtonClickListener(listener: () -> Unit) {
        onMessageButtonClickListener = listener
    }
} 
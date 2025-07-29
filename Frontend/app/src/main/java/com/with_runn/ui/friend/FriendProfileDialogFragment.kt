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
import com.with_runn.R

class FriendProfileDialogFragment : DialogFragment() {
    private var friendName: String = ""
    private var onMessageButtonClickListener: (() -> Unit)? = null

    companion object {
        private const val ARG_FRIEND_NAME = "friend_name"
        private const val ARG_PERSONALITY_TAG = "personality_tag"
        private const val ARG_PERSONALITY_TAGS = "personality_tags"
        private const val ARG_IMAGE_RES_ID = "image_res_id"

        fun newInstance(
            friendName: String,
            personalityTag: String,
            personalityTags: ArrayList<String>,
            imageResId: Int
        ): FriendProfileDialogFragment {
            val fragment = FriendProfileDialogFragment()
            val args = Bundle()
            args.putString(ARG_FRIEND_NAME, friendName)
            args.putString(ARG_PERSONALITY_TAG, personalityTag)
            args.putStringArrayList(ARG_PERSONALITY_TAGS, personalityTags)
            args.putInt(ARG_IMAGE_RES_ID, imageResId)
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

        // 데이터 가져오기
        friendName = arguments?.getString(ARG_FRIEND_NAME) ?: ""
        val personalityTag = arguments?.getString(ARG_PERSONALITY_TAG) ?: ""
        val personalityTags = arguments?.getStringArrayList(ARG_PERSONALITY_TAGS) ?: arrayListOf()
        val imageResId = arguments?.getInt(ARG_IMAGE_RES_ID, R.drawable.maru) ?: R.drawable.maru

        setupUI(friendName, personalityTag, personalityTags, imageResId)
    }

    private fun setupUI(
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

        // 팔로우 버튼 설정
        val followButton = view?.findViewById<TextView>(R.id.follow_button)
        followButton?.setOnClickListener {
            // 팔로우 기능 구현
        }

        // 메시지 버튼 설정
        val messageButton = view?.findViewById<TextView>(R.id.message_button)
        messageButton?.setOnClickListener {
            // 메시지 버튼 클릭 리스너 호출
            onMessageButtonClickListener?.invoke()
            dismiss() // 다이얼로그 닫기
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
                   val blockDialog = BlockUserDialogFragment.newInstance(friendName)
                   blockDialog.show(childFragmentManager, "BlockUserDialog")
               }
        
        popupView.findViewById<View>(R.id.report_button)?.setOnClickListener {
            // 신고하기 Activity 실행
            popupWindow.dismiss()
            val intent = Intent(requireContext(), ReportActivity::class.java)
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
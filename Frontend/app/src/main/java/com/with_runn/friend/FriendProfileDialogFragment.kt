package com.with_runn.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.with_runn.R

class FriendProfileDialogFragment : DialogFragment() {

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
        val friendName = arguments?.getString(ARG_FRIEND_NAME) ?: ""
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
            // 메뉴 기능 구현
        }

        // 팔로우 버튼 설정
        val followButton = view?.findViewById<TextView>(R.id.follow_button)
        followButton?.setOnClickListener {
            // 팔로우 기능 구현
        }

        // 메시지 버튼 설정
        val messageButton = view?.findViewById<TextView>(R.id.message_button)
        messageButton?.setOnClickListener {
            // 메시지 기능 구현
        }


    }
} 
package com.with_runn.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.with_runn.R
import com.with_runn.ui.friend.viewmodel.RecommendedFriendViewModel

class BlockUserDialogFragment : DialogFragment() {
    private lateinit var viewModel: RecommendedFriendViewModel

    companion object {
        private const val ARG_USER_NAME = "user_name"
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userName: String, userId: Int): BlockUserDialogFragment {
            val fragment = BlockUserDialogFragment()
            val args = Bundle()
            args.putString(ARG_USER_NAME, userName)
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
        return inflater.inflate(R.layout.dialog_block_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 설정
        viewModel = ViewModelProvider(this)[RecommendedFriendViewModel::class.java]

        // Dialog 설정
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // 사용자 정보 가져오기
        val userName = arguments?.getString(ARG_USER_NAME) ?: "사용자"
        val userId = arguments?.getInt(ARG_USER_ID, 0)

        // 제목 설정
        val titleText = view.findViewById<TextView>(R.id.block_title)
        titleText.text = "${userName}님을 차단하시겠습니까?"

        // 취소 버튼 설정
        val cancelButton = view.findViewById<TextView>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            dismiss()
        }

        // 차단 버튼 설정
        val blockButton = view.findViewById<TextView>(R.id.block_button)
        blockButton.setOnClickListener {
            if (userId != null && userId > 0) {
                // 차단하기 API 호출
                viewModel.blockUser(userId)
                // 로딩 상태 표시
                blockButton.text = "차단 중..."
                blockButton.isEnabled = false
            } else {
                // userId가 유효하지 않은 경우
                android.widget.Toast.makeText(requireContext(), "사용자 정보를 찾을 수 없습니다.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // Observer 설정
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.blockResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                // 차단 성공 시 메시지 표시 후 다이얼로그 닫기
                android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_SHORT).show()
                viewModel.clearBlockResult()
                dismiss()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // 에러 메시지를 더 명확하게 처리
                val message = when {
                    it.contains("이미 차단된 사용자") -> {
                        // 이미 차단된 경우: 오류가 아니라 안내 메시지로 처리하고 다이얼로그 닫기
                        "이미 차단된 대상입니다."
                    }
                    it.contains("차단할 수 없는 상태") -> {
                        "현재 차단할 수 없는 상태입니다."
                    }
                    it.contains("네트워크") -> {
                        "네트워크 연결을 확인해주세요."
                    }
                    else -> {
                        "차단에 실패했습니다: $it"
                    }
                }
                
                android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
                // 이미 차단된 경우에는 다이얼로그를 닫고, 그 외에는 버튼을 복구
                if (message == "이미 차단된 대상입니다.") {
                    viewModel.clearError()
                    dismiss()
                } else {
                    viewModel.clearError()
                    // 차단 버튼을 다시 활성화
                    val blockButton = view?.findViewById<TextView>(R.id.block_button)
                    blockButton?.let { button ->
                        button.text = "차단하기"
                        button.isEnabled = true
                    }
                }
            }
        }
    }
} 
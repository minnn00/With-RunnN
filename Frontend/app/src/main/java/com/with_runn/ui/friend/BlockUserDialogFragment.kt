package com.with_runn.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.with_runn.R

class BlockUserDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_USER_NAME = "user_name"

        fun newInstance(userName: String): BlockUserDialogFragment {
            val fragment = BlockUserDialogFragment()
            val args = Bundle()
            args.putString(ARG_USER_NAME, userName)
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

        // Dialog 설정
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // 사용자 이름 가져오기
        val userName = arguments?.getString(ARG_USER_NAME) ?: "사용자"

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
            // 차단 확인 다이얼로그 표시
            dismiss()
            val confirmDialog = BlockConfirmDialogFragment()
            confirmDialog.show(parentFragmentManager, "BlockConfirmDialog")
        }
    }
} 
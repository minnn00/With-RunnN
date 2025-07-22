package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.with_runn.databinding.CourseShareBinding

class CourseShareBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: CourseShareBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CourseShareBinding.inflate(inflater, container, false)
        return _binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기에 탭 전환, 어댑터 연결, 선택 처리 등을 추가할 예정
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

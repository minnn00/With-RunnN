package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.with_runn.databinding.FragmentCourseDetailBinding

class CourseDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var course: WalkCourse

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달받은 데이터 꺼내기
        course = arguments?.getParcelable("course") ?: return

        // 더미 바인딩 예시 (내일 레이아웃 오면 수정)
        binding.textTitle.text = course.title
        binding.textDescription.text =
            "이 코스는 ${course.distance}, ${course.time} 정도 소요돼요."

        // 예시용: 시간 텍스트만 미리 바인딩
        binding.textTimeValue.text = course.time.replace("분", "M")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(course: WalkCourse): CourseDetailBottomSheet {
            val args = Bundle().apply {
                putParcelable("course", course)
            }
            return CourseDetailBottomSheet().apply {
                arguments = args
            }
        }
    }
}
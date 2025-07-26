package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.with_runn.databinding.FragmentCourseDetailBinding
import android.util.Log
import android.widget.TextView

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

        course = arguments?.getParcelable("course") ?: return

        // 기본 바인딩
        binding.imageCourse.setImageResource(course.imageResId)
        binding.textTitle.text = course.title
        binding.textDescription.text = "우리 동네 코스 소개\n${course.distance}, ${course.time} 소요됩니다."
        binding.textTimeValue.text = course.time.replace("분", "M")

        // 태그 동적 추가
        val tagContainer = binding.layoutTags
        tagContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        course.tags.take(2).forEach { tag ->
            val tagView = inflater.inflate(R.layout.item_tag, tagContainer, false) as TextView
            tagView.text = tag
            tagContainer.addView(tagView)
        }

        // 버튼 클릭 리스너
        binding.btnScrap.setOnClickListener {
            Log.d("CourseDetail", "스크랩 클릭됨: ${course.title}")
            CourseStorage.addScrap(course)
        }

        binding.btnLike.setOnClickListener {
            Log.d("CourseDetail", "좋아요 클릭됨: ${course.title}")
            CourseStorage.addLike(course)
        }

        binding.btnShare.setOnClickListener {
            Log.d("CourseDetail", "공유 버튼 클릭됨")
            val bottomSheet = CourseShareBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, "CourseShareBottomSheet")
        }
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
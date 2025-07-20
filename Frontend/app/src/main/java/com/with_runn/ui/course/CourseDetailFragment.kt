package com.with_runn.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.with_runn.databinding.FragmentCourseDetailBinding
import android.widget.TextView
import android.util.Log
import com.with_runn.R


class CourseDetailFragment : Fragment() {

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var course: WalkCourse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        course = arguments?.getParcelable("course") ?: return

        // 바인딩 (기존 유지)
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

        // ✅ 🔥 ViewBinding 대신 findViewById로 버튼 연결
        val scrapButton = view.findViewById<View>(R.id.btnScrap)
        val likeButton = view.findViewById<View>(R.id.btnLike)

        scrapButton.setOnClickListener {
            Log.d("CourseDetail", "스크랩 클릭됨: ${course.title}")
            CourseStorage.addScrap(course)
        }

        likeButton.setOnClickListener {
            Log.d("CourseDetail", "좋아요 클릭됨: ${course.title}")
            CourseStorage.addLike(course)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

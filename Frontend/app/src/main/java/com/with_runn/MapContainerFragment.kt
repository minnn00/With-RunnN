package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.with_runn.databinding.FragmentCourseMapBinding

class MapContainerFragment : Fragment() {

    private var _binding: FragmentCourseMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔙 뒤로가기 버튼 동작
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 🗺️ 지도 이미지 클릭 시 바텀시트 띄우기
        binding.imageMap.setOnClickListener {
            val dummyCourse = WalkCourse(
                title = "반려견과 한강 산책",
                tags = listOf("#이웃사촌", "#같이산책해요"),
                imageResId = R.drawable.image,  // 꼭 존재하는 이미지로!
                distance = "2.0km",
                time = "35분"
            )

            val bottomSheet = CourseDetailBottomSheet.newInstance(dummyCourse)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

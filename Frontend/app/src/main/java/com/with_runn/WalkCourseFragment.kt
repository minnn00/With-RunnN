package com.with_runn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentWalkCourseBinding
import androidx.navigation.fragment.findNavController

class WalkCourseFragment : Fragment() {

    private var _binding: FragmentWalkCourseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 더보기 버튼 클릭 시 네비게이션
        binding.textLocalMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_localMoreFragment)
        }
        binding.textHotMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_hotMoreFragment)
        }

        // 우리 동네 산책코스 - LocalCourseAdapter 연결
        val localAdapter = LocalCourseAdapter(getDummyLocalCourses()) { course ->
            val walkCourse = WalkCourse(
                title = course.title,
                tags = listOf(course.tag),
                imageResId = course.imageRes,
                distance = "2.0km",  // 임시값
                time = "30분"        // 임시값
            )

            val bundle = Bundle().apply {
                putParcelable("course", walkCourse)
            }

            findNavController().navigate(R.id.mapContainerFragment, bundle)
        }

        binding.recyclerLocalCourse.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = localAdapter
        }

        // 떠오르는 산책코스 - HotCourseAdapter 연결
        val hotAdapter = HotCourseAdapter(getDummyHotCourses()) { course ->
            // 1. HotCourse -> WalkCourse로 변환
            val walkCourse = WalkCourse(
                title = course.title,
                tags = course.tags,
                imageResId = course.imageRes,
                distance = course.distance,
                time = course.time
            )

            // 2. Bundle에 담아서 전달
            val bundle = Bundle().apply {
                putParcelable("course", walkCourse)
            }
            findNavController().navigate(R.id.mapContainerFragment, bundle)


        }

        binding.recyclerHotCourse.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = hotAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 우리 동네 산책코스 더미 데이터 (LocalCourse 타입)
    private fun getDummyLocalCourses(): List<LocalCourse> {
        return listOf(
            LocalCourse(R.drawable.image, "#초보자추천", "망원한강공원"),
            LocalCourse(R.drawable.image, "#풍경좋음", "연남동 코스"),
            LocalCourse(R.drawable.image, "#도심속자연", "홍제천 산책길")
        )
    }

    // 떠오르는 산책코스 더미 데이터 (HotCourse 타입)
    private fun getDummyHotCourses(): List<HotCourse> {
        return listOf(
            HotCourse(R.drawable.image, "반려견과 한강 산책", listOf("#자연친화", "#탐색활동"), "2.0km", "35분"),
            HotCourse(R.drawable.image, "연트럴파크 데이트길", listOf("#후각자극", "#도심산책"), "1.5km", "20분"),
            HotCourse(R.drawable.image, "서울숲 동물친화코스", listOf("#풍경좋음", "#초보자추천"), "3.4km", "45분")
        )
    }
}

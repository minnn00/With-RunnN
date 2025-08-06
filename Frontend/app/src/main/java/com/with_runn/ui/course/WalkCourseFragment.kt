package com.with_runn.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.with_runn.data.LocalCourse
import com.with_runn.data.HotCourse
import com.with_runn.ActivityViewModel
import com.with_runn.data.WalkCourse
import com.with_runn.databinding.FragmentWalkCourseBinding
import com.with_runn.data.viewmodel.WalkCourseViewModel
import com.with_runn.R

class WalkCourseFragment : Fragment() {

    private var _binding: FragmentWalkCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WalkCourseViewModel
    private lateinit var localPreviewAdapter: LocalCourseAdapter
    private lateinit var risingPreviewAdapter: HotCourseAdapter
    private val activityVM : ActivityViewModel by activityViewModels()

    private lateinit var localAdapter: LocalCourseAdapter
    private lateinit var hotAdapter: HotCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activityVM.setBottomNavVisibility(true)

        _binding = FragmentWalkCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WalkCourseViewModel::class.java]

        // 미리보기 어댑터만 연결
        localPreviewAdapter = LocalCourseAdapter(mutableListOf()) { course ->
            val bundle = Bundle().apply { putInt("courseId", course.id) }
        // Local Course Adapter 초기화
        val localAdapter = LocalCourseAdapter(emptyList<LocalCourse>().toMutableList()) { course ->
            val walkCourse = WalkCourse(
                title = course.title,
                tags = listOf(course.tag),
                imageResId = course.imageRes,
                distance = "2.0km",  // 임시값
                time = "30분"        // 임시값
            )
//            val bundle = Bundle().apply {
//                putParcelable("course", walkCourse)
//            }
//            findNavController().navigate(R.id.courseManageFragment, bundle)
            val bundle = Bundle().apply {
                putInt("courseId", 1) // TODO: CourseItem의 Id를 전달
            }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        binding.recyclerLocalCourse.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = localPreviewAdapter
            adapter = localAdapter
        }

        // Hot Course Adapter 초기화
        hotAdapter = HotCourseAdapter(emptyList()) { course ->
            val walkCourse = WalkCourse(
                title = course.title,
                tags = course.tags,
                imageResId = course.imageRes,
                distance = course.distance,
                time = course.time
            )
//            val bundle = Bundle().apply {
//                putParcelable("course", walkCourse)
//            }
//            findNavController().navigate(R.id.courseManageFragment, bundle)
            val bundle = Bundle().apply {
                putInt("courseId", 1) // TODO: CourseItem의 Id를 전달
            }
            //val bundle = Bundle().apply {
            //    putInt("courseId", course.id)
            //}
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        //  미리보기 데이터만 관찰
        viewModel.neighborhoodPreview.observe(viewLifecycleOwner) { previewList ->
            Log.d("WalkCourseFragment", "우리동네 미리보기 데이터: ${previewList.size}개")
            localPreviewAdapter.updateData(
                previewList.map {
                    LocalCourse(
                        id = it.courseId,
                        title = it.name,
                        tag = it.keyword.firstOrNull() ?: "#산책",
                        imageRes = R.drawable.image,
                        imageUrl = it.courseImage
                    )
                }.toMutableList()
            )
        }

        // 더보기 버튼 → 전체 리스트 Fragment로 이동
        binding.textLocalMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_localMoreFragment)
        }

        binding.textRisingMore.setOnClickListener {
            Log.d("WalkCourseFragment", "떠오르는 더보기 클릭됨!")
            findNavController().navigate(R.id.action_walkCourse_to_hotMoreFragment)
        }

        risingPreviewAdapter = HotCourseAdapter(mutableListOf()) { course ->
            val bundle = Bundle().apply { putInt("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        binding.recyclerRisingCourse.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = risingPreviewAdapter
        }

        viewModel.risingPreview.observe(viewLifecycleOwner) { previewList ->
            Log.d("WalkCourseFragment", "떠오르는 미리보기 데이터 size: ${previewList.size} / 내용: $previewList")
            risingPreviewAdapter.updateData(
                previewList.map {
                    HotCourse(
                        id = it.courseId,
                        title = it.name,
                        tags = it.keyword,
                        distance = "",
                        time = it.time,
                        imageRes = R.drawable.image,
                        imageUrl = it.courseImage
                    )
                }
            )
        }

        // (떠오르는 코스, 전체 코스 등은 이 Fragment에서는 **아예 X**)
        // API 호출
        Log.d("WalkCourseFragment", "미리보기 API 호출 provinceId=11")
        viewModel.loadDummyCourses()
        //viewModel.fetchNeighborhoodPreview(provinceId = 11)
        viewModel.neighborhoodCourses.observe(viewLifecycleOwner) { courses ->
            Log.d("WalkCourseFragment", "더미 데이터 관찰! ${courses.size}개")
            localPreviewAdapter.updateData(
                courses.map {
                    LocalCourse(
                        id = it.id,
                        title = it.title,
                        tag = it.tags.firstOrNull() ?: "#산책",
                        imageRes = it.imageResId,
                        imageUrl = it.imageUrl
                    )
                }.toMutableList()
            )
        }
        viewModel.fetchRisingPreview()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

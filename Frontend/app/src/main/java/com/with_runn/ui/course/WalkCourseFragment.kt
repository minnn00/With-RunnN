package com.with_runn.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.with_runn.data.WalkCourse
import com.with_runn.databinding.FragmentWalkCourseBinding
import com.with_runn.data.viewmodel.WalkCourseViewModel
import com.with_runn.R
import com.with_runn.data.LocalCourse
import com.with_runn.data.HotCourse

class WalkCourseFragment : Fragment() {

    private var _binding: FragmentWalkCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WalkCourseViewModel
    private lateinit var localAdapter: LocalCourseAdapter
    private lateinit var hotAdapter: HotCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WalkCourseViewModel::class.java]

        // Local Course Adapter 초기화
        val localAdapter = LocalCourseAdapter(emptyList<LocalCourse>().toMutableList()) { course ->
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

        // Hot Course Adapter 초기화
        hotAdapter = HotCourseAdapter(emptyList()) { course ->
            val walkCourse = WalkCourse(
                title = course.title,
                tags = course.tags,
                imageResId = course.imageRes,
                distance = course.distance,
                time = course.time
            )
            val bundle = Bundle().apply {
                putParcelable("course", walkCourse)
            }
            findNavController().navigate(R.id.mapContainerFragment, bundle)
        }

        binding.recyclerHotCourse.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = hotAdapter
        }

        // 더보기 버튼 클릭 시 이동
        binding.textLocalMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_localMoreFragment)
        }
        binding.textHotMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_hotMoreFragment)
        }

        // LiveData 관찰
        viewModel.neighborhoodCourses.observe(viewLifecycleOwner) { courses ->
            localAdapter.updateData(courses.map {
                LocalCourse(
                    imageRes = R.drawable.image,
                    tag = it.tags.firstOrNull() ?: "#산책",
                    title = it.title
                )
            })
        }

        viewModel.risingCourses.observe(viewLifecycleOwner) { courses ->
            hotAdapter.updateData(courses.map {
                HotCourse(
                    imageRes = R.drawable.image,
                    title = it.title,
                    tags = it.tags,
                    distance = it.distance,
                    time = it.time
                )
            })

        }

        // 실제 데이터 요청
        viewModel.fetchNeighborhoodCourses()
        viewModel.fetchRisingCourses()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

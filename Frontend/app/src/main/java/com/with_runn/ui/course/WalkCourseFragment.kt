package com.with_runn.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.R
import com.with_runn.ActivityViewModel
import com.with_runn.databinding.FragmentWalkCourseBinding

class WalkCourseFragment : Fragment() {
    private val walkCourseVM: WalkCourseViewModel by activityViewModels()
    private val activityVM: ActivityViewModel by activityViewModels()

    private var _binding: FragmentWalkCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var localCourseAdapter: LocalCourseAdapter
    private lateinit var risingCourseAdapter: HotCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkCourseBinding.inflate(inflater, container, false)
        activityVM.setBottomNavVisibility(true)
        activityVM.setUpperToolbarVisibility(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localCourseAdapter = LocalCourseAdapter(mutableListOf()) { course ->
            val bundle = Bundle().apply { putInt("courseId", course.courseId) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        risingCourseAdapter = HotCourseAdapter(emptyList()) { course ->
            val bundle = Bundle().apply { putInt("courseId", course.courseId) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        with(binding) {
            recyclerLocalCourse.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = localCourseAdapter
            }
            recyclerRisingCourse.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = risingCourseAdapter
                isNestedScrollingEnabled = false
            }
            localMoreBtn.setOnClickListener { findNavController().navigate(R.id.localMoreFragment) }
            risingMoreBtn.setOnClickListener { findNavController().navigate(R.id.hotMoreFragment) }
        }

        // Local 섹션
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            walkCourseVM.nearbyCourses.collect { list ->
                if (list.isEmpty()) {
                    showLocalEmpty("인근에서 산책 코스를 찾지 못했습니다")
                } else {
                    showLocalList(list)
                }
            }
        }
        // Rising 섹션
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            walkCourseVM.risingCourses.collect { list ->
                if (list.isEmpty()) {
                    showRisingEmpty("인기 코스를 찾지 못했습니다")
                } else {
                    showRisingList(list)
                }
            }
        }

        val provinceId = activityVM.firstRegion.value.id ?: 9
        val cityId = activityVM.secondRegion.value?.id
        val townId = activityVM.thirdRegion.value?.id
        walkCourseVM.fetchNearbyCourses(provinceId, cityId, townId)
        walkCourseVM.fetchRisingCourses()
    }

    private fun showLocalList(list: List<com.with_runn.data.course.CourseSummary>) {
        binding.emptyLocal.root.visibility = View.GONE
        binding.recyclerLocalCourse.visibility = View.VISIBLE
        localCourseAdapter.updateData(list, 10)
    }
    private fun showLocalEmpty(msg: String) {
        binding.recyclerLocalCourse.visibility = View.GONE
        binding.emptyLocal.root.visibility = View.VISIBLE
        binding.emptyLocal.emptyMessage.text = msg
    }

    private fun showRisingList(list: List<com.with_runn.data.course.CourseSummary>) {
        binding.emptyRising.root.visibility = View.GONE
        binding.recyclerRisingCourse.visibility = View.VISIBLE
        risingCourseAdapter.updateData(list, 10)
    }
    private fun showRisingEmpty(msg: String) {
        binding.recyclerRisingCourse.visibility = View.GONE
        binding.emptyRising.root.visibility = View.VISIBLE
        binding.emptyRising.emptyMessage.text = msg
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

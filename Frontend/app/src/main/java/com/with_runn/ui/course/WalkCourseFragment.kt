package com.with_runn.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.ActivityViewModel
import com.with_runn.R
import com.with_runn.data.HotCourse
import com.with_runn.data.LocalCourse
import com.with_runn.data.viewmodel.WalkCourseViewModel
import com.with_runn.databinding.FragmentWalkCourseBinding
import androidx.lifecycle.lifecycleScope

class WalkCourseFragment : Fragment() {

    private var _binding: FragmentWalkCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WalkCourseViewModel
    private lateinit var localPreviewAdapter: LocalCourseAdapter
    private lateinit var risingPreviewAdapter: HotCourseAdapter

    private val activityVM: ActivityViewModel by activityViewModels()

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

        // === 우리동네 미리보기 (가로) ===
        localPreviewAdapter = LocalCourseAdapter(mutableListOf()) { course ->
            val bundle = Bundle().apply { putInt("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        binding.recyclerLocalCourse.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = localPreviewAdapter
        }

        // === 떠오르는 미리보기 (세로) ===
        risingPreviewAdapter = HotCourseAdapter(mutableListOf()) { course ->
            val bundle = Bundle().apply { putInt("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        binding.recyclerRisingCourse.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = risingPreviewAdapter
            isNestedScrollingEnabled = false
        }

        // === Observe ===
        viewModel.neighborhoodPreview.observe(viewLifecycleOwner) { previewList ->
            Log.d("WalkCourseFragment", "우리동네 미리보기 원본 데이터: $previewList")
            localPreviewAdapter.updateData(
                previewList.map {
                    LocalCourse(
                        id = it.courseId,
                        title = it.name ?: "(제목 없음)",
                        tag = it.keyword?.joinToString(" ") { k -> "#$k" } ?: "#산책",
                        imageRes = R.drawable.image,
                        imageUrl = it.courseImage
                    )
                }.toMutableList()
            )
        }

        viewModel.risingPreview.observe(viewLifecycleOwner) { previewList ->
            Log.d("WalkCourseFragment", "떠오르는 미리보기 데이터: ${previewList.size}개")
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

        // === 더보기 버튼 ===
        binding.textLocalMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_localMoreFragment)
        }
        binding.textRisingMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_hotMoreFragment)
        }

        // === 실제 API 호출 ===
        // provinceId는 실제 값으로 교체 가능
        viewModel.fetchRisingPreview()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityVM.selectedProvinceId.collect { pid ->
                Log.d("WalkCourseFragment", "우리동네 미리보기 호출 provinceId=$pid")
                viewModel.fetchNeighborhoodPreview(provinceId = 1)
            }
        }
        viewModel.fetchRisingPreview()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

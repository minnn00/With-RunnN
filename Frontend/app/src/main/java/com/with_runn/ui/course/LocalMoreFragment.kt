package com.with_runn.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.ActivityViewModel
import com.with_runn.R
import com.with_runn.data.course.CourseSummary
import com.with_runn.databinding.FragmentLocalMoreBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class LocalMoreFragment : Fragment() {

    private var _binding: FragmentLocalMoreBinding? = null
    private val binding get() = _binding!!

    private val walkCourseVM: WalkCourseViewModel by activityViewModels()
    private val activityVM: ActivityViewModel by activityViewModels()

    private lateinit var verticalAdapter: HotCourseAdapter

    // 검색 쿼리 흐름
    private val queryFlow = MutableStateFlow("")

    private var provinceId: Int = 9
    private var cityId: Int? = null
    private var townId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalMoreBinding.inflate(inflater, container, false)
        activityVM.setBottomNavVisibility(false)
        activityVM.setUpperToolbarVisibility(false)

        provinceId = activityVM.firstRegion.value.id ?: 9
        cityId = activityVM.secondRegion.value?.id
        townId = activityVM.thirdRegion.value?.id
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verticalAdapter = HotCourseAdapter(emptyList()) { course ->
            val b = Bundle().apply { putInt("courseId", course.courseId) }
            findNavController().navigate(R.id.courseDetailFragment, b)
        }

        binding.recyclerLocalMore.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = verticalAdapter
            isNestedScrollingEnabled = false
        }

        // 텍스트 변경 → queryFlow 업데이트
        binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
            queryFlow.value = text?.toString()?.trim().orEmpty()
        }

        // 단일 파이프라인: 쿼리에 따라 UI 갱신
        viewLifecycleOwner.lifecycleScope.launch {
            queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { q ->
                    if (q.isBlank()) {
                        // 기본 목록: nearby 표시
                        showList(walkCourseVM.nearbyCourses.value)
                    } else {
                        // 검색
                        if (provinceId <= 0) {
                            showEmpty("지역을 먼저 선택해 주세요")
                            return@collectLatest
                        }
                        // 네트워크 호출 트리거
                        walkCourseVM.fetchSearchNearbyCourses(
                            provinceId = provinceId,
                            cityId = cityId,
                            townId = townId,
                            keyword = q
                        )
                        // 최신 쿼리 기준으로만 결과 반영
                        walkCourseVM.searchNearbyCourses.collectLatest { result ->
                            // collectLatest라서 새 쿼리가 들어오면 이전 수집은 자동 취소
                            if (q.isBlank()) return@collectLatest // 사용자가 바로 지운 경우
                            if (result.isNullOrEmpty()) {
                                showEmpty("검색 결과가 없어요")
                            } else {
                                showList(result)
                            }
                            // 한 번 반영 후 루프 탈출 (다음 쿼리 대기)
                            return@collectLatest
                        }
                    }
                }
        }

        // 초기 데이터 없으면 nearby 호출
        if (walkCourseVM.nearbyCourses.value.isEmpty()) {
            walkCourseVM.fetchNearbyCourses(
                provinceId = provinceId,
                cityId = cityId,
                townId = townId
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showList(list: List<CourseSummary>) {
        binding.emptyView.root.visibility = View.GONE
        binding.recyclerLocalMore.visibility = View.VISIBLE
        verticalAdapter.updateData(list)
    }

    private fun showEmpty(message: String) {
        binding.recyclerLocalMore.visibility = View.GONE
        binding.emptyView.root.visibility = View.VISIBLE
        binding.emptyView.emptyMessage.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

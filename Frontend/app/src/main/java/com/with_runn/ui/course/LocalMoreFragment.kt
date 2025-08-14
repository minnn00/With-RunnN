package com.with_runn.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.R
import com.with_runn.ActivityViewModel
import com.with_runn.data.viewmodel.LocalMoreViewModel
import com.with_runn.databinding.FragmentLocalMoreBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

private const val NO_KM = "– km"

class LocalMoreFragment : Fragment() {

    private val TAG = "LocalMoreFrag"

    private var _binding: FragmentLocalMoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocalMoreViewModel by viewModels()
    private val activityVM: ActivityViewModel by activityViewModels()

    private var currentProvinceId: Int? = null
    private lateinit var adapter: WalkCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated()")

        adapter = WalkCourseAdapter(mutableListOf()) { item ->
            Log.d(TAG, "itemClick id=${item.id} title=${item.title}")
            val b = Bundle().apply { putParcelable("course", item) }
            findNavController().navigate(R.id.courseManageFragment, b)
        }
        binding.recyclerLocalMore.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLocalMore.adapter = adapter

        // 선택된 provinceId가 바뀔 때만 호출 (하드코딩 제거)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityVM.firstRegion.collect { province ->
                    // selectedProvinceId 가 Int 라면 다음 줄은 그냥: val pid = pidNullable
                    val pid = province?.id ?: 0      // Int? 가능성 대비
                    Log.d(TAG, "selectedProvinceId → $pid")
                    currentProvinceId = pid
                    if (pid > 0) {
                        viewModel.fetchNearbyCourses(provinceId = pid)  // Int
                    } else {
                        Log.w(TAG, "pid<=0 → 서버 호출하지 않음")
                    }
                }
        }

        // 검색 (현재 pID로 검색, pID 없으면 안내)
        binding.editTextSearch.setOnEditorActionListener { _, _, _ ->
            val keyword = binding.editTextSearch.text.toString().trim()
            if (keyword.isEmpty()) return@setOnEditorActionListener false

            val pid = currentProvinceId ?: 0
            if (pid <= 0) {
                Toast.makeText(requireContext(), "지역을 먼저 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnEditorActionListener true
            }
            Log.d(TAG, "searchCourses(provinceId=$pid, keyword='$keyword')")
            viewModel.searchCourses(provinceId = pid, keyword = keyword)
            true
        }

        // 목록 옵저버
        viewModel.localCourses.observe(viewLifecycleOwner) { list ->
            Log.d(TAG, "localCourses size=${list.size} sample=${list.firstOrNull()}")
            val ui = list.map { wc ->
                if (wc.distance.isNullOrBlank()) wc.copy(distance = NO_KM) else wc
            }
            adapter.updateItems(ui.toMutableList())
            Log.d(TAG, "adapter.items=${adapter.itemCount}")
        }

        // 검색 결과 옵저버 (ViewModel이 WalkCourse로 매핑해서 내려줌)
        viewModel.searchCoursesUi.observe(viewLifecycleOwner) { list ->
            Log.d(TAG, "searchCoursesUi size=${list.size} sample=${list.firstOrNull()}")
            val ui = list.map { wc ->
                if (wc.distance.isNullOrBlank()) wc.copy(distance = NO_KM) else wc
            }
            adapter.updateItems(ui.toMutableList())
        }

        binding.btnBack.setOnClickListener {
            Log.d(TAG, "back pressed")
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

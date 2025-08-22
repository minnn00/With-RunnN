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
import com.with_runn.databinding.FragmentHotMoreBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HotMoreFragment : Fragment() {

    private var _binding: FragmentHotMoreBinding? = null
    private val binding get() = _binding!!

    private val walkCourseVM: WalkCourseViewModel by activityViewModels()
    private val activityVM: ActivityViewModel by activityViewModels()

    private lateinit var verticalAdapter: HotCourseAdapter

    private val queryFlow = MutableStateFlow("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotMoreBinding.inflate(inflater, container, false)
        activityVM.setBottomNavVisibility(false)
        activityVM.setUpperToolbarVisibility(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verticalAdapter = HotCourseAdapter(emptyList()) { course ->
            val b = Bundle().apply { putInt("courseId", course.courseId) }
            findNavController().navigate(R.id.courseDetailFragment, b)
        }

        binding.recyclerHotMore.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = verticalAdapter
            isNestedScrollingEnabled = false
        }

        binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
            queryFlow.value = text?.toString()?.trim().orEmpty()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { q ->
                    if (q.isBlank()) {
                        showList(walkCourseVM.risingCourses.value)
                    } else {
                        walkCourseVM.fetchSearchRisingCourses(q)
                        walkCourseVM.searchRisingCourses.collectLatest { result ->
                            if (q.isBlank()) return@collectLatest
                            if (result.isNullOrEmpty()) {
                                showEmpty("검색 결과가 없어요")
                            } else {
                                showList(result)
                            }
                            return@collectLatest
                        }
                    }
                }
        }

        if (walkCourseVM.risingCourses.value.isEmpty()) {
            walkCourseVM.fetchRisingCourses()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showList(list: List<CourseSummary>) {
        binding.emptyView.root.visibility = View.GONE
        binding.recyclerHotMore.visibility = View.VISIBLE
        verticalAdapter.updateData(list)
    }

    private fun showEmpty(message: String) {
        binding.recyclerHotMore.visibility = View.GONE
        binding.emptyView.root.visibility = View.VISIBLE
        binding.emptyView.emptyMessage.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

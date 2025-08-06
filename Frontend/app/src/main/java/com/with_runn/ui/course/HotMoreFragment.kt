package com.with_runn.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentHotMoreBinding
import com.with_runn.data.viewmodel.HotMoreViewModel
import com.with_runn.R
import com.with_runn.data.WalkCourse

class HotMoreFragment : Fragment() {

    private var _binding: FragmentHotMoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HotMoreViewModel by viewModels()
    private lateinit var adapter: WalkCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WalkCourseAdapter(emptyList()) { item ->
            val bundle = Bundle().apply {
                putParcelable("course", item)
            }
            findNavController().navigate(R.id.courseManageFragment, bundle)
        }

        binding.recyclerHotMore.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHotMore.adapter = adapter
        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            val keyword = binding.editTextSearch.text.toString()
            if (keyword.isNotBlank()) {
                viewModel.searchRisingCourses(keyword)
                true
            } else {
                false
            }
        }

        // API 호출
        viewModel.fetchRisingCourses()

        // LiveData observe
        viewModel.hotCourses.observe(viewLifecycleOwner) { list ->
            android.util.Log.d("HotMoreFragment", "RecyclerView 데이터 size: ${list.size} / $list")
            adapter.updateItems(list)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.updateItems(results.map {
                WalkCourse(
                    id = it.courseId,
                    title = it.name,
                    tags = it.keyword,            // List<String>
                    imageResId = R.drawable.image,
                    distance = "",
                    time = it.time,
                    isScrapped = false,           // 필요에 따라 값 설정
                    isLiked = false,              // 필요에 따라 값 설정
                    imageUrl = it.courseImage
                )
            }.toMutableList())
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

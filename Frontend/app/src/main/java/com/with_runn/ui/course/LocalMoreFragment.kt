package com.with_runn.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.with_runn.databinding.FragmentLocalMoreBinding
import com.with_runn.data.viewmodel.LocalMoreViewModel
import com.with_runn.R
import com.with_runn.data.WalkCourse

class LocalMoreFragment : Fragment() {

    private var _binding: FragmentLocalMoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocalMoreViewModel by viewModels()
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

        adapter = WalkCourseAdapter(mutableListOf(),
            onItemClick = { item ->
                val bundle = Bundle().apply {
                    putParcelable("course", item)
                }
                findNavController().navigate(R.id.courseManageFragment, bundle)
            }
        )

        binding.recyclerLocalMore.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLocalMore.adapter = adapter
        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            val keyword = binding.editTextSearch.text.toString()
            if (keyword.isNotBlank()) {
                viewModel.searchCourses(
                    provinceId = 11,
                    keyword = keyword
                )
                true
            } else {
                false
            }
        }


        viewModel.fetchNearbyCourses(provinceId = 11)
        android.util.Log.d("LocalMoreFragment", "API 호출: fetchNearbyCourses provinceId=11")

        viewModel.localCourses.observe(viewLifecycleOwner) { list ->
            android.util.Log.d("LocalMoreFragment", "RecyclerView 데이터 size: ${list.size} / $list")
            adapter.updateItems(list)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (results.isNullOrEmpty()) {
                adapter.updateItems(results.map {
                    WalkCourse(
                        id = it.courseId,
                        title = it.name,
                        tags = it.keyword, // WalkCourse의 tags가 List<String>이면 이렇게
                        imageResId = R.drawable.image,
                        distance = "",
                        time = it.time,
                        isScrapped = false, // 필요시
                        isLiked = false,    // 필요시
                        imageUrl = it.courseImage
                    )
                }.toMutableList())
            } else {
                // 검색 결과 표시
                adapter.updateItems(results.map {
                    WalkCourse(
                        id = it.courseId,
                        title = it.name,
                        tags = it.keyword, // WalkCourse의 tags가 List<String>이면 이렇게
                        imageResId = R.drawable.image,
                        distance = "",
                        time = it.time,
                        isScrapped = false, // 필요시
                        isLiked = false,    // 필요시
                        imageUrl = it.courseImage
                    )
                }.toMutableList())
            }
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

package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentHotMoreBinding
import com.with_runn.WalkCourseAdapter
import com.with_runn.WalkCourse
import androidx.navigation.fragment.findNavController

class HotMoreFragment : Fragment() {

    private var _binding: FragmentHotMoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: WalkCourseAdapter
    private lateinit var hotList: List<WalkCourse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 더미 데이터 (distance, time 포함)
        hotList = listOf(
            WalkCourse(
                imageResId = R.drawable.img_dog_meeting,
                title = "우리 동네 강아지 모임",
                tags = listOf("#이웃사촌", "#같이산책해요"),
                distance = "1.5km",
                time = "15분"
            ),
            WalkCourse(
                imageResId = R.drawable.img_healing_trail,
                title = "발바닥 힐링길",
                tags = listOf("#부드러운산책", "#노령견"),
                distance = "2.5km",
                time = "25분"
            ),
            WalkCourse(
                imageResId = R.drawable.img_playground,
                title = "놀이터 한 바퀴",
                tags = listOf("#애견운동장", "#사회성기르기"),
                distance = "1.3km",
                time = "12분"
            )
        )

        // 어댑터에 클릭 콜백 전달
        adapter = WalkCourseAdapter(hotList,
            onItemClick = { item ->
            }
        )

        binding.recyclerHotMore.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHotMore.adapter = adapter

        // 뒤로가기 버튼 처리
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.with_runn.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentLocalMoreBinding
import com.with_runn.WalkCourseAdapter
import com.with_runn.WalkCourse
import androidx.navigation.fragment.findNavController


class LocalMoreFragment : Fragment() {

    private var _binding: FragmentLocalMoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: WalkCourseAdapter
    private lateinit var localList: List<WalkCourse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 더미 데이터 - WalkCourse 생성자에 맞게 distance, time 추가
        localList = listOf(
            WalkCourse(
                title = "우리 동네 강아지 모임",
                tags = listOf("#이웃사촌", "#같이산책해요"),
                imageResId = R.drawable.img_dog_meeting,
                distance = "1.5km",
                time = "15분"
            ),
            WalkCourse(
                title = "발바닥 힐링길",
                tags = listOf("#부드러운산책", "#노령견"),
                imageResId = R.drawable.img_healing_trail,
                distance = "2.5km",
                time = "25분"
            ),
            WalkCourse(
                title = "놀이터 한 바퀴",
                tags = listOf("#애견운동장", "#사회성키우기"),
                imageResId = R.drawable.img_playground,
                distance = "1.3km",
                time = "12분"
            )
            // 추가 데이터 필요시 여기에 계속 추가 가능
        )

        adapter = WalkCourseAdapter(localList,
            onItemClick = { item ->
                val bundle = Bundle().apply {
                    putParcelable("course", item)
                }
                findNavController().navigate(R.id.mapContainerFragment, bundle)
            }
        )

        binding.recyclerLocalMore.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLocalMore.adapter = adapter

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

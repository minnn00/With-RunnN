package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.with_runn.databinding.FragmentLocalMoreBinding
import com.with_runn.WalkCourseAdapter
import com.with_runn.WalkCourse

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

        // 예시 데이터
        localList = listOf(
            WalkCourse("우리 동네 강아지 모임", listOf("#이웃사촌", "#같이산책해요"), R.drawable.img_dog_meeting),
            WalkCourse("발바닥 힐링길", listOf("#부드러운산책", "#노령견"), R.drawable.img_healing_trail),
            WalkCourse("놀이터 한 바퀴", listOf("#애견운동장", "#사회성키우기"), R.drawable.img_playground),
            WalkCourse("냄새탐험로드", listOf("#후각자극", "#탐색활동"), R.drawable.img_dog_meeting),
            WalkCourse("풀내음 산책로", listOf("#풀냄새가득", "#자연친화"), R.drawable.img_healing_trail),
            WalkCourse("쫄래쫄래 감성로드", listOf("#느긋한산책", "#산책bgm_on"), R.drawable.img_dog_meeting),
        )

        adapter = WalkCourseAdapter(localList)
        binding.recyclerLocalMore.adapter = adapter

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

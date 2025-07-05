package com.with_runn

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.with_runn.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private var _binding : FragmentMapBinding? = null;
    private val binding get() = _binding!!

    private val keywords = listOf("병원", "약국", "반려동물용품", "미용", "식당", "당식", "용미", "품용물동려반", "국약", "원병")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateChips(
            chipGroup = binding.chipGroup,
            inflater = layoutInflater,
            keywords = keywords
        ){
            chip -> onChipClick(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null;
    }

    fun onChipClick(chip: Chip){
        Log.d("ChipClick", "${chip.text} Clicked")
        // TODO: Naver API에 키워드로 검색 요청해서 결과 표시하기
    }
}
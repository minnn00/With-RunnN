package com.with_runn.map.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentSearchResultBinding

class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    val sampleData = listOf<SearchResultItem>(
        SearchResultItem("연남약국", true, 1100, 2300, true),
        SearchResultItem("별빛약국", true, 900, 2030, false),
        SearchResultItem("임시약국", false, 1200, 2400, false),
        SearchResultItem("예시약국", true, 0, 2400, true),
        SearchResultItem("불법약국", true, 2300, 400, true),
        SearchResultItem("점심약국", false, 1100, 1300, false),
        SearchResultItem("국약남연", false, 11, 32, false),
        SearchResultItem("국약빛별", false, 90, 302, false),
        SearchResultItem("국약시임", true, 21, 42, true),
        SearchResultItem("국약시예", false, 0, 42, false),
        SearchResultItem("국약법불", false, 32, 40, false),
        SearchResultItem("국약심점", true, 11, 31, true)
    )
    private val searchResultAdapter = SearchResultAdapter(
        sampleData,
        onItemClick = { item -> onClickItem(item) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchResultRcv.apply{
            adapter = searchResultAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null;
    }

    private fun onClickItem(item: SearchResultItem){
        Log.d("TEST", "${item.name} clicked!")
        // TODO: 추출된 키워드로 장소 정보 제공
        // TODO: Fragment 교체, Backstack에 추가
    }
}
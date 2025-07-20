package com.with_runn

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding? = null;
    private val binding get() = _binding!!;

    val recentSearchAdapter = RecentSearchAdapter(
        onClick = {keyword -> onClickItem(keyword)},
        onDelete = {pos -> onDeleteItem(pos)}
    )

    val sampleData = listOf("홍대입구 산책로", "홍대 산책코스", "연남 산책코스", "연남동", "서울 산책");

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recentSearchRcv.apply{
            adapter = recentSearchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recentSearchAdapter.submitList(sampleData)

        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null;
    }

    private fun setListeners(){
        binding.apply{
            backBtn.setOnClickListener {
                findNavController().popBackStack()
                Log.d("TEST", "뒤로가기 클릭됨")
            }
        }
    }

    private fun onClickItem(keyword: String){
        Log.d("TEST", "$keyword clicked!")
        // TODO: 추출된 키워드로 Naver API 검색 수행
        findNavController().popBackStack()
    }

    private fun onDeleteItem(pos: Int){
        Log.d("TEST", "$pos deleted!")
        // TODO: config에서 키워드 삭제 후 업데이트
        // TODO: RCV 업데이트
    }

}
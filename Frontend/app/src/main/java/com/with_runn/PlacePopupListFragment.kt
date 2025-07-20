package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentPlacePopupListBinding

class PlacePopupListFragment : Fragment() {

    private var _binding: FragmentPlacePopupListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlacePopupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacePopupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyPlaces = mutableListOf(
            PlaceItem("빵집", "치아바타 좋아요 같이 빵 먹어요!", R.drawable.ic_pin_red),
            PlaceItem("카페", "커피는 디카페인으로 부탁해요 ", R.drawable.ic_pin_blue),
            PlaceItem("공원", "산책 마무리는 공원이 최고죠 ", R.drawable.ic_pin_black)
        )


        adapter = PlacePopupAdapter(dummyPlaces) { position ->
            adapter.removeItem(position) // 닫기 버튼 눌렀을 때 아이템 제거
        }

        binding.recyclerPlacePopup.adapter = adapter
        binding.recyclerPlacePopup.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

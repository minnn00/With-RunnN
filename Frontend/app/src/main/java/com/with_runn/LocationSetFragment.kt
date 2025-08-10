package com.with_runn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentLocationSetBinding
import com.with_runn.ui.LocationSetAdapter
import com.with_runn.ui.RegionItem

class LocationSetFragment : Fragment() {

    // 시/도 샘플
    private val sampleFirstRegion = listOf(
        RegionItem(1, "서울특별시"),
        RegionItem(2, "부산광역시"),
        RegionItem(3, "경기도")
    )

    // 시/군/구 샘플 (서울특별시 기준)
    private val sampleSecondRegionSeoul = listOf(
        RegionItem(101, "강남구"),
        RegionItem(102, "종로구"),
        RegionItem(103, "마포구")
    )

    // 읍/면/동 샘플 (강남구 기준)
    private val sampleThirdRegionGangnam = listOf(
        RegionItem(1001, "역삼동"),
        RegionItem(1002, "삼성동"),
        RegionItem(1003, "논현동")
    )


    private var _binding : FragmentLocationSetBinding? = null
    private val binding get() = _binding!!

    private lateinit var firstRegionAdapter : LocationSetAdapter
    private lateinit var secondRegionAdapter : LocationSetAdapter
    private lateinit var thirdRegionAdapter : LocationSetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) 어댑터 생성 (콜백은 TODO로 비워둠)
        firstRegionAdapter = LocationSetAdapter { id ->
            // TODO: 첫 번째 리스트 아이템 클릭 시 처리
        }
        secondRegionAdapter = LocationSetAdapter { id ->
            // TODO: 두 번째 리스트 아이템 클릭 시 처리
        }
        thirdRegionAdapter = LocationSetAdapter { id ->
            // TODO: 세 번째 리스트 아이템 클릭 시 처리
        }

        binding.apply {
            // 첫 번째
            rcvRegion01.layoutManager = LinearLayoutManager(requireContext())
            rcvRegion01.adapter = firstRegionAdapter

            // 두 번째
            rcvRegion02.layoutManager = LinearLayoutManager(requireContext())
            rcvRegion02.adapter = secondRegionAdapter

            // 세 번째
            rcvRegion03.layoutManager = LinearLayoutManager(requireContext())
            rcvRegion03.adapter = thirdRegionAdapter
        }

        firstRegionAdapter.submitList(sampleFirstRegion)
        secondRegionAdapter.submitList(sampleSecondRegionSeoul)
        thirdRegionAdapter.submitList(sampleThirdRegionGangnam)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
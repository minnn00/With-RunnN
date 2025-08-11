package com.with_runn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.data.region.RegionResponse
import com.with_runn.databinding.FragmentLocationSetBinding
import kotlinx.coroutines.launch

class LocationSetFragment : Fragment() {

    // 시/도 샘플
    private val sampleFirstRegion = listOf(
        RegionResponse(1, "서울특별시"),
        RegionResponse(2, "부산광역시"),
        RegionResponse(3, "경기도")
    )

    // 시/군/구 샘플 (서울특별시 기준)
    private val sampleSecondRegionSeoul = listOf(
        RegionResponse(101, "강남구"),
        RegionResponse(102, "종로구"),
        RegionResponse(103, "마포구")
    )

    // 읍/면/동 샘플 (강남구 기준)
    private val sampleThirdRegionGangnam = listOf(
        RegionResponse(1001, "역삼동"),
        RegionResponse(1002, "삼성동"),
        RegionResponse(1003, "논현동")
    )


    private var _binding : FragmentLocationSetBinding? = null
    private val binding get() = _binding!!

    private val activityVM : ActivityViewModel by activityViewModels()
    private val locationSetVM : LocationSetViewModel by viewModels()

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

        activityVM.setBottomNavVisibility(false)
        activityVM.setUpperToolbarVisibility(false)

        // 1) 어댑터 생성
        firstRegionAdapter = LocationSetAdapter { item ->
            locationSetVM.setFirstRegion(item)
            thirdRegionAdapter.submitList(emptyList())

            viewLifecycleOwner.lifecycleScope.launch {
                val token = activityVM.accessToken.value.orEmpty()
                if (token.isEmpty()) return@launch
                val cities = locationSetVM.fetchCities(token, item.id)
                secondRegionAdapter.submitList(cities)
            }
        }
        secondRegionAdapter = LocationSetAdapter { item ->
            locationSetVM.setSecondRegion(item)
            viewLifecycleOwner.lifecycleScope.launch {
                val token = activityVM.accessToken.value.orEmpty()
                if (token.isEmpty()) return@launch
                val towns = locationSetVM.fetchTowns(token, item.id)
                thirdRegionAdapter.submitList(towns)
            }
        }
        thirdRegionAdapter = LocationSetAdapter { item ->
            locationSetVM.setThirdRegion(item)
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

            backBtn.setOnClickListener { findNavController().popBackStack() }
            btnReset.setOnClickListener {
                locationSetVM.apply {
                    setFirstRegion(null)
                    setSecondRegion(null)
                    setThirdRegion(null)
                }
                
                firstRegionAdapter.reset()
                secondRegionAdapter.submitList(emptyList())
                thirdRegionAdapter.submitList(emptyList())
            }
            btnSave.setOnClickListener {
                val l1 = locationSetVM.firstRegion.value ?: return@setOnClickListener
                val l2 = locationSetVM.secondRegion.value ?: return@setOnClickListener
                val l3 = locationSetVM.thirdRegion.value  ?: return@setOnClickListener

                viewLifecycleOwner.lifecycleScope.launch {
                    val token = activityVM.accessToken.value.orEmpty()
                    if (token.isEmpty()) return@launch
                    val success = locationSetVM.saveSelection(token, l1.id, l2.id, l3.id)
                    if (success) {
                        activityVM.updateSelectedRegion(l1, l2, l3) // 전역 상태 반영
                        findNavController().popBackStack()
                    } else {
                        // TODO: 실패 처리 (토스트 등)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            locationSetVM.firstRegion.collect { data ->
                binding.apply {
                    textRegion.text = data?.name ?: ""
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            locationSetVM.secondRegion.collect { data ->
                binding.apply {
                    var text = locationSetVM.firstRegion.value?.name  ?: ""
                    text += " ${data?.name ?: ""}"
                    textRegion.text = text
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            locationSetVM.thirdRegion.collect { data ->
                binding.apply {
                    var text = locationSetVM.firstRegion.value?.name ?: ""
                    text += " ${locationSetVM.secondRegion.value?.name ?: ""}"
                    text += " ${data?.name ?: ""}"
                    textRegion.text = text

                    val enabled = (data != null)
                    btnSave.isEnabled = enabled
                    btnSave.setBackgroundResource(
                        if (enabled) R.drawable.bg_btn_filled else R.drawable.bg_btn_filled_inactive
                    )
                }

            }
        }

//        firstRegionAdapter.submitList(sampleFirstRegion)
//        secondRegionAdapter.submitList(sampleSecondRegionSeoul)
//        thirdRegionAdapter.submitList(sampleThirdRegionGangnam)

        viewLifecycleOwner.lifecycleScope.launch {
            activityVM.loadToken()
            val token = activityVM.accessToken.value.orEmpty()
            if (token.isEmpty()) return@launch

            val provinces = locationSetVM.fetchProvinces(token)
            firstRegionAdapter.submitList(provinces)
            secondRegionAdapter.submitList(emptyList())
            thirdRegionAdapter.submitList(emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
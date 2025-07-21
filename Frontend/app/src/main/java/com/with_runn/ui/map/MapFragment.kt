package com.with_runn.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.naver.maps.map.NaverMap
import com.with_runn.R
import com.with_runn.ui.map.search.SearchResultFragment
import com.with_runn.databinding.FragmentMapBinding
import com.with_runn.populateChips

class MapFragment : Fragment() {

    private var _binding : FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap

    private val keywords = listOf("병원", "약국", "반려동물용품", "미용", "식당", "당식", "용미", "품용물동려반", "국약", "원병")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            googleMap = it

            val isGranted = (
                    ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(
                                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                    )

            if(isGranted){
                moveToMyLocation()
            }else{
                Log.e("PERMISSION ERROR", "LACK PERMISSION")
            }
            //TODO: CHECK PERMISSION AT START AND UPDATE VIEWMODEL

            googleMap.apply {
                isMyLocationEnabled = true

                uiSettings.apply {
                    isCompassEnabled = false
                    isMyLocationButtonEnabled = false
                    isMapToolbarEnabled = false
                }
            }
        }

        populateChips(
            chipGroup = binding.chipGroup,
            inflater = layoutInflater,
            keywords = keywords,
            chipLayoutRes = R.layout.view_chip
        ) { chip ->
            onChipClick(chip)
        }

        setListeners()

        setupSpeedDial()

        setupBottomSheet()
    }

    override fun onStart() {
        binding.mapView.onStart()
        super.onStart()
    }
    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        binding.mapView.onDestroy()
        _binding = null
        super.onDestroyView()
    }

    override fun onLowMemory() {
        binding.mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding.mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun onChipClick(chip: Chip){
        Log.d("ChipClick", "${chip.text} Clicked")
        // TODO: Google API에 키워드로 검색 요청해서 결과 표시하기
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun moveToMyLocation(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let{
                val latLng = LatLng(it.latitude, it.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }
        }
    }

    private fun setListeners(){
        binding.apply{
            viewSearchBox.searchBox.setOnClickListener{
                findNavController().navigate(R.id.action_mapFragment_to_searchFragment)
            }

            fabToggleMylocation.setOnClickListener {
                val isGranted = (
                    ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )

                if(isGranted){
                    moveToMyLocation()
                }else{
                    Log.e("PERMISSION ERROR", "LACK PERMISSION")
                }
                //TODO: CHECK PERMISSION AT START AND UPDATE VIEWMODEL
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBottomSheet(){
        childFragmentManager.beginTransaction()
            .replace(R.id.bottom_sheet_content, SearchResultFragment())
            .commit()

        val bottomSheet = binding.viewHomeIndicator.bottomSheetBehaviour
        val behavior = BottomSheetBehavior.from(bottomSheet)

        behavior.isFitToContents = false
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.halfExpandedRatio = 0.5f
        behavior.isHideable = false
        behavior.isDraggable = false
        behavior.expandedOffset = 50

        var initY = 0f
        var lastY = 0f
        binding.viewHomeIndicator.bottomSheetHandle.setOnTouchListener { v, event ->
            when (event.action){
                MotionEvent.ACTION_DOWN ->{
                    initY = event.rawY
                    lastY = initY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    lastY = event.rawY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick()

                    val deltaY = lastY - initY
                    val state = behavior.state

                    if(deltaY < -20){
                        behavior.state = when(state){
                            BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_HALF_EXPANDED
                            BottomSheetBehavior.STATE_HALF_EXPANDED -> BottomSheetBehavior.STATE_EXPANDED
                            else -> BottomSheetBehavior.STATE_EXPANDED
                        }
                    }else if(deltaY > 20){
                        behavior.state = when(state){
                            BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_HALF_EXPANDED
                            BottomSheetBehavior.STATE_HALF_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
                            else -> BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                    true
                }else -> {false}
            }
        }
    }

    private fun setupSpeedDial() {
        val speedDialView = binding.speedDial

        speedDialView.addActionItem(
            createSpeedDialActionItem(
                id = R.id.create_course,
                icon = R.drawable.ic_add_course,
                label = "산책 코스 생성하기"
            )
        )
        speedDialView.addActionItem(
            createSpeedDialActionItem(
                id = R.id.load_course,
                icon = R.drawable.ic_load_course,
                label = "산책 코스 불러오기"
            )
        )
        speedDialView.addActionItem(
            createSpeedDialActionItem(
                id = R.id.edit_course,
                icon = R.drawable.ic_edit_course,
                label = "산책 코스 수정하기"
            )
        )

        // 서브 메뉴 클릭 시 로그 송출
        speedDialView.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                //TODO: 화면 연결
                R.id.create_course -> {
                    Log.d("FAB", "테스트 1 클릭됨")
                    speedDialView.close()
                    return@setOnActionSelectedListener true
                }
                R.id.load_course -> {
                    Log.d("FAB", "테스트 2 클릭됨")
                    findNavController().navigate(R.id.action_mapFragment_to_courseManageFragment)
                    speedDialView.close()
                    return@setOnActionSelectedListener true
                }
                R.id.edit_course -> {
                    Log.d("FAB", "테스트 3 클릭됨")
                    speedDialView.close()
                    return@setOnActionSelectedListener true
                }
                else -> false
            }
        }


    }
    private fun createSpeedDialActionItem(
        id: Int,
        icon: Int,
        label: String
    ): SpeedDialActionItem{
        return SpeedDialActionItem
            .Builder(id, icon)
            .setLabel(label)
            .setFabBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_700))
            .setLabelBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_050))
            .setLabelColor(ContextCompat.getColor(requireContext(), R.color.gray_950))
            .setFabImageTintColor(ContextCompat.getColor(requireContext(), R.color.gray_050))
            .create()
    }



}
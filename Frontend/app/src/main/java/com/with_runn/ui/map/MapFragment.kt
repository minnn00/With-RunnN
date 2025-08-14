package com.with_runn.ui.map

import FacilityItem
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresPermission
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.with_runn.ActivityViewModel
import com.with_runn.R
import com.with_runn.databinding.FragmentMapBinding
import com.with_runn.dp
import com.with_runn.getCurrentTimeInt
import com.with_runn.populateChips
import kotlinx.coroutines.launch
import com.with_runn.ui.map.search.SearchResultFragment
import com.with_runn.ui.map.search.SearchResultItem
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.with_runn.parseOperatingHours
import kotlin.math.min
import kotlin.math.roundToInt


class MapFragment : Fragment() {

    private var _binding : FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val mapViewModel : MapViewModel by viewModels()
    private val activityVM : ActivityViewModel by activityViewModels()

    private lateinit var googleMap: GoogleMap

    private lateinit var behavior : BottomSheetBehavior<View>

    private lateinit var searchResultFragment: SearchResultFragment

    private val markerIconCache = mutableMapOf<String, BitmapDescriptor>()

    private val keywords = listOf(
        "병원",
        "약국",
        "반려동물용품",
        "미용",
        "식당",
        "카페",
        "숙소",
        "박물관",
        "미술관",
        "여행지",
        "문예회관",
        "위탁관리")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel.setLocationPermission(checkLocationPermission())

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityVM.setBottomNavVisibility(true)
        activityVM.setUpperToolbarVisibility(false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            googleMap = it

            @SuppressLint("MissingPermission")
            if(mapViewModel.locationPermissionGranted.value){
                moveToMyLocation()
                googleMap.isMyLocationEnabled = true
            }else{
                Log.e("PERMISSION ERROR", "LACK PERMISSION")
                // TODO: 권한 재요청 후 승인 시 moveToMyLocation 수행
            }

            googleMap.apply {
                setOnPoiClickListener { poi ->
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)
                            .icon(getMarkerIcon(null))
                    )
                    mapViewModel.setTempMarker(marker)
                    marker?.showInfoWindow()
                }

                uiSettings.apply {
                    isCompassEnabled = false
                    isMyLocationButtonEnabled = false
                    isMapToolbarEnabled = false
                }
            }

            startMarkerCoroutine()
        }

        populateChips(
            chipGroup = binding.chipGroup,
            inflater = layoutInflater,
            keywords = keywords,
            chipLayoutRes = R.layout.view_chip
        ) { chip ->
            onChipClick(chip)
        }

        behavior = BottomSheetBehavior.from(binding.viewHomeIndicator.bottomSheetBehaviour)

        setBackPressedCallback()
        setListeners()
        setupFragments()
        setupSpeedDial()
        setupBottomSheet()
    }

    private fun setupFragments(){
        searchResultFragment = SearchResultFragment()
        searchResultFragment.setOnPlaceSelectedListener { item ->
            Log.d("PlaceSelected", "선택된 장소 ID: $item")
            val latLng = LatLng(item.latitude, item.longitude)
            behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

        }

        childFragmentManager.beginTransaction()
            .add(R.id.bottom_sheet_content, searchResultFragment, "SearchResult")
            .commitNow()

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
        _binding?.mapView?.onLowMemory()
        super.onLowMemory()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.mapView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun startMarkerCoroutine(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                mapViewModel.markers.collect { markerList ->
                    googleMap.clear()
                    markerList.forEach { item ->
                        addMarker(item.position, item.title, item.snippet)
                    }
                }
            }
        }
    }

    private fun addMarker(position: LatLng, title: String? = null, snippet: String? = null) : Marker? {
        val markerOptions = MarkerOptions()
            .position(position)
            .title(title)
            .snippet(snippet)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_basic_pin))

        val marker = googleMap.addMarker(markerOptions)
        return marker
    }

    private fun onChipClick(chip: Chip) {
        val category = when (chip.text.toString()) {
            "병원" -> "동물병원"
            "약국" -> "동물약국"
            "반려동물용품" -> "반려동물용품"
            "미용" -> "미용"
            "식당" -> "식당"
            "카페" -> "카페"
            "숙소" -> "호텔"
            "박물관" -> "박물관"
            "미술관" -> "미술관"
            "여행지" -> "여행지"
            "문예회관" -> "문예회관"
            "위탁관리" -> "위탁관리"
            else -> null
        }

        if (category == null) {
            Log.e("ChipClick", "알 수 없는 카테고리: ${chip.text}")
            return
        }

        val current = mapViewModel.currentChipType.value

        if (current == category) {
            mapViewModel.setCurrentChipType("")
            getMarkerIcon(category)
            mapViewModel.clearFacilityList()
            googleMap.clear()
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }else{
            mapViewModel.setCurrentChipType(category)

            val sido = activityVM.firstRegion.value?.name ?: "서울특별시"
            val gugun = activityVM.secondRegion.value?.name
            val dong = activityVM.thirdRegion.value?.name

            mapViewModel.loadFacilities(
                sido = null,
                gugun = null,
                dong = null,
                category = category
            ){ success ->
                if (success){
                    val items = mapViewModel.facilityList.value


                    val filteredData = items
                        .asSequence()
                        .filter { item ->
                            // 지역 필터
                            (item.sido_name == sido) &&
                            (gugun == null || item.gugun_name == gugun) &&
                            (dong == null || item.dong_name == dong) &&
                            (item.ctg3_name == category)
                        }.toList()

                    showFacilityMarkers(filteredData)

                    val results = filteredData
                        .map { item ->
                            val (openInt, closeInt) = parseOperatingHours(item.weekday_oper_time)
                            Log.d(
                                "OPER_HOURS",
                                "name=${item.fac_name} raw=\"${item.weekday_oper_time}\" -> parsed=($openInt,$closeInt)")
                            val now = getCurrentTimeInt()
                            val isOpen = if (closeInt < openInt) {
                                now >= openInt || now < closeInt
                            } else {
                                now in openInt until closeInt
                            }

                            SearchResultItem(
                                placeId = "${item.latitude},${item.longitude}",
                                name = item.fac_name ?: "(이름 없음)",
                                isOpen = isOpen,
                                openTime = openInt,
                                closeTime = closeInt,
                                isParkable = item.parking_poss_yn?.uppercase() == "Y",
                                imageUri = null,
                                latitude = item.latitude?.toDouble() ?: 0.0,
                                longitude = item.longitude?.toDouble() ?: 0.0
                            )
                        }
                    searchResultFragment.setResults(results)
                    behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }else{
                    Snackbar.make(requireView(), "검색 결과가 없습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun checkLocationPermission() : Boolean{
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return (fineLocationPermission && coarseLocationPermission)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun moveToMyLocation(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let{
                val latLng = LatLng(it.latitude, it.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getMyLocation(callback: (LatLng) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback(LatLng(location.latitude, location.longitude))
            } else {
                // fallback
                callback(LatLng(37.5665, 126.9780))
            }
        }
    }

    private fun setListeners(){
        binding.apply{
//            viewSearchBox.searchBox.setOnClickListener{
//                findNavController().navigate(R.id.action_mapFragment_to_searchFragment)
//            }

            fabToggleMylocation.setOnClickListener {
                @SuppressLint("MissingPermission")
                if(mapViewModel.locationPermissionGranted.value){
                    moveToMyLocation()
                }else{
                    Log.e("PERMISSION ERROR", "LACK PERMISSION")
                }
                //TODO: CHECK PERMISSION AT START AND UPDATE VIEWMODEL
            }

            fabToggleMylocationExpanded.setOnClickListener {
                @SuppressLint("MissingPermission")
                if(mapViewModel.locationPermissionGranted.value){
                    moveToMyLocation()
                }else{
                    Log.e("PERMISSION ERROR", "LACK PERMISSION")
                }
                //TODO: CHECK PERMISSION AT START AND UPDATE VIEWMODEL
            }
        }
    }

    private fun showFacilityMarkers(items: List<FacilityItem>) {
        googleMap.clear()

        val cat = mapViewModel.currentChipType.value.takeIf { it.isNotBlank() }
        val icon = getMarkerIcon(cat)
            ?: BitmapDescriptorFactory.fromResource(R.drawable.ic_basic_pin)

        for (item in items) {
            val lat = item.latitude?.toDoubleOrNull()
            val lng = item.longitude?.toDoubleOrNull()
            if (lat != null && lng != null) {
                val position = LatLng(lat, lng)
                val title = item.fac_name ?: "이름 없음"
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(title)
                        .icon(icon)
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBottomSheet(){
        behavior.isFitToContents = false
        behavior.halfExpandedRatio = 0.45f
        behavior.isHideable = true
        behavior.peekHeight = 150.dp

//        var initY = 0f
//        var lastY = 0f

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val binding = this@MapFragment.binding ?: return
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        googleMap.clear()
                        binding.chipGroup.clearCheck()
                        mapViewModel.setCurrentChipType("")

                        activityVM.setBottomNavVisibility(true)
                        binding.speedDial.visibility = View.VISIBLE
                        binding.fabToggleMylocation.visibility = View.VISIBLE
                        binding.fabToggleMylocationExpanded.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        activityVM.setBottomNavVisibility(false)
                        binding.speedDial.visibility = View.VISIBLE
                        binding.fabToggleMylocation.visibility = View.VISIBLE
                        binding.fabToggleMylocationExpanded.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        activityVM.setBottomNavVisibility(false)
                        binding.speedDial.visibility = View.GONE
                        binding.fabToggleMylocation.visibility = View.GONE
                        binding.fabToggleMylocationExpanded.visibility = View.VISIBLE
                    }
                }
            }


            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        binding.viewHomeIndicator.bottomSheetBehaviour.post{
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun setBackPressedCallback(){
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    when(behavior.state){
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }

                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }

                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            behavior.state = BottomSheetBehavior.STATE_HIDDEN
                            // 검색 결과 초기화
                            binding.chipGroup.clearCheck()
                            mapViewModel.setCurrentChipType("")
                            searchResultFragment.setResults(emptyList())
                            googleMap.clear()
                        }

                        else -> {
                            isEnabled = false
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            })
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

        // 서브 메뉴 클릭 시 로그 송출
        speedDialView.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.create_course -> {
                    findNavController().navigate(R.id.action_mapFragment_to_courseManageFragment)
                    Log.d("FAB", "테스트 1 클릭됨")
                    speedDialView.close()
                    return@setOnActionSelectedListener true
                }
                R.id.load_course -> {
                    Log.d("FAB", "테스트 2 클릭됨")
                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNav.selectedItemId = R.id.mypage_graph
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

    private fun getIconResForCategory(category: String?): Int? = when (category) {
        "동물병원" -> R.drawable.ic_vat
        "동물약국" -> R.drawable.ic_pharmacy
        "반려동물용품" -> R.drawable.ic_merch
        "미용"     -> R.drawable.ic_design
        "식당"     -> R.drawable.ic_food
        "카페"     -> R.drawable.ic_food
        "호텔"     -> R.drawable.ic_rentalhouse
        "박물관"   -> R.drawable.ic_curtural
        "미술관"   -> R.drawable.ic_curtural
        "여행지"   -> R.drawable.ic_curtural
        "문예회관" -> R.drawable.ic_curtural
        "위탁관리" -> R.drawable.ic_boarding
        else -> R.drawable.ic_basic_pin
    }

    private fun getMarkerIcon(category: String?, maxSizeDp: Float? = null): BitmapDescriptor? {
        // 캐시 히트
        if (category != null) {
            markerIconCache[category]?.let { return it }
        }

        val resId = getIconResForCategory(category) ?: return null
        val drawable = getDrawable(requireContext(), resId) ?: return null

        val dm = resources.displayMetrics
        val density = dm.density

        // 드로어블의 고유 크기 (벡터면 intrinsic, -1일 수 있어요)
        val intrinsicW = (drawable.intrinsicWidth).takeIf { it > 0 } ?: (24 * density).toInt()
        val intrinsicH = (drawable.intrinsicHeight).takeIf { it > 0 } ?: (24 * density).toInt()

        val (outW, outH) = if (maxSizeDp == null) {
            // 스케일 없음: 고유 크기 그대로
            intrinsicW to intrinsicH
        } else {
            // 긴 변을 maxSizeDp로 맞추고 비율 유지
            val maxPx = (maxSizeDp * density).toInt().coerceAtLeast(1)
            val ratio = min(maxPx / intrinsicW.toFloat(), maxPx / intrinsicH.toFloat())
            (intrinsicW * ratio).roundToInt().coerceAtLeast(1) to
                    (intrinsicH * ratio).roundToInt().coerceAtLeast(1)
        }

        val bitmap = drawable.toBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val desc = BitmapDescriptorFactory.fromBitmap(bitmap)
        if (category != null) markerIconCache[category] = desc
        return desc
    }


}
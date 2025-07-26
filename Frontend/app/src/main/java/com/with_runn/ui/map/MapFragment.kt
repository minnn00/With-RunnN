package com.with_runn.ui.map

import FacilityItem
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.os.Trace.isEnabled
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresPermission
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.with_runn.R
import com.with_runn.databinding.FragmentMapBinding
import com.with_runn.dp
import com.with_runn.getCurrentTimeInt
import com.with_runn.parseOperatingHours
import com.with_runn.populateChips
import kotlinx.coroutines.launch
import com.with_runn.ui.map.search.SearchResultFragment
import com.with_runn.ui.map.search.SearchResultItem
import kotlin.math.absoluteValue

class MapFragment : Fragment() {

    private var _binding : FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val mapViewModel : MapViewModel by viewModels()

    private lateinit var googleMap: GoogleMap
    private lateinit var placesClient: PlacesClient

    private lateinit var behavior : BottomSheetBehavior<View>


//    private lateinit var placeSearchFragment: PlaceSearchFragment
//    private lateinit var placeDetailsFragment: PlaceDetailsFragment
    private lateinit var searchResultFragment: SearchResultFragment

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

        placesClient = Places.createClient(requireContext())

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            googleMap = it

            @SuppressLint("MissingPermission")
            if(mapViewModel.locationPermissionGranted.value){
                moveToMyLocation()
                googleMap.isMyLocationEnabled = true
            }else{
                Log.e("PERMISSION ERROR", "LACK PERMISSION")
            }

            googleMap.apply {
                setOnPoiClickListener { poi ->
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)
                    )
                    marker?.showInfoWindow()
                }

                uiSettings.apply {
                    isCompassEnabled = false
                    isMyLocationButtonEnabled = false
                    isMapToolbarEnabled = false
                }
            }

            startMarkerCoroutine()

//            searchByText("카페")
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

//        placeSearchFragment = PlaceSearchFragment.newInstance(PlaceSearchFragment.ALL_CONTENT)
//        placeDetailsFragment = PlaceDetailsFragment.newInstance(PlaceDetailsFragment.ALL_CONTENT)
//
//        placeSearchFragment.apply{
//            selectable = true
//
//            registerListener(
//                object : PlaceSearchFragmentListener{
//                    override fun onLoad(places: List<Place>) {
//                        // TODO: ViewModel 업데이트
//                    }
//                    override fun onRequestError(e: Exception) {
//                        Log.e("PlaceSearch", "검색 실패", e)
//                    }
//                    override fun onPlaceSelected(place: Place) {
//                        Log.d("PlaceSelected", "Place selected: $place")
//                        place.id?.let {
//                            placeDetailsFragment.loadWithPlaceId(it)
//                            showDetailsFragment()
//                        } ?: run{
//                            Log.e("PlaceDetails", "Failed to load place details")
//                            Snackbar.make(
//                                requireView(),
//                                "세부 정보를 불러올 수 없습니다.",
//                                Snackbar.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            )
//        }
//
//        placeDetailsFragment.setPlaceLoadListener(
//            object: PlaceLoadListener{
//                override fun onSuccess(place: Place) {
//                    Log.d("PlaceDetails", "Place loaded: ${place.displayName}")
//                }
//
//                override fun onFailure(e: java.lang.Exception) {
//                    Log.e("PlaceDetails", "Failed to load place details", e)
//                }
//            }
//        )
//
//        childFragmentManager.beginTransaction()
//            .add(R.id.bottom_sheet_content, placeSearchFragment, "search")
//            .add(R.id.bottom_sheet_content, placeDetailsFragment, "details")
//            .hide(placeDetailsFragment)
//            .hide(placeSearchFragment)
//            .commitNow()
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

//    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
//    private fun searchNearbyPlaces(type: String){
//        if (!mapViewModel.locationPermissionGranted.value) return
//
//        getMyLocation { center ->
//            val placeFields = listOf(
//                Place.Field.ID,
//                Place.Field.DISPLAY_NAME,
//                Place.Field.OPENING_HOURS,
//                Place.Field.BUSINESS_STATUS,
//                Place.Field.PARKING_OPTIONS,
//                Place.Field.PHOTO_METADATAS
//            )
//
//            val radius = CircularBounds.newInstance(center, 1000.0)
//
//            val includeType = listOf(type)
//
//            val searchNearbyRequest = SearchNearbyRequest.builder(radius, placeFields)
//                .setIncludedTypes(includeType)
//                .setMaxResultCount(20)
//                .build()
//
//            placeSearchFragment.configureFromSearchNearbyRequest(searchNearbyRequest)
//            showSearchResultFragment()
//        }
//
//
//    }

//    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
//    private fun searchByText(text: String){
//        if (!mapViewModel.locationPermissionGranted.value) return
//        getMyLocation { center ->
//            val placeFields = listOf(
//                Place.Field.ID,
//                Place.Field.DISPLAY_NAME,
//                Place.Field.OPENING_HOURS,
//                Place.Field.BUSINESS_STATUS,
//                Place.Field.PARKING_OPTIONS,
//                Place.Field.PHOTO_METADATAS
//            )
//
//            val latDistance = 0.5 / 111.0     // 0.5km = 500m
//            val lngDistance = 0.5 / (111.0 * cos(Math.toRadians(center.latitude)))
//
//            val southWest = LatLng(center.latitude - latDistance, center.longitude - lngDistance)
//            val northEast = LatLng(center.latitude + latDistance, center.longitude + lngDistance)
//            val bounds = RectangularBounds.newInstance(southWest, northEast)
//
//            val searchByTextRequest = SearchByTextRequest.builder(text, placeFields)
//                .setMaxResultCount(20)
//                .setLocationRestriction(bounds)
//                .build()
//
//
//            placeSearchFragment.configureFromSearchByTextRequest(searchByTextRequest)
//            showSearchResultFragment()
//        }
//    }

    private fun startMarkerCoroutine(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                mapViewModel.markers.collect { markerList ->
                    googleMap.clear()
                    markerList.forEach { item ->
                        addMarker(item.position, item.title, item.snippet)
                    }
                    googleMap.setOnMarkerClickListener { marker ->
                        Log.d("MARKER_CLICK", "Clicked Marker at: ${marker.position}")
                        // TODO: 마커 정보 ViewModel에서 추출 및 팝업

                        false
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
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))

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
            mapViewModel.clearFacilityList()
            googleMap.clear()
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }else{
            mapViewModel.setCurrentChipType(category)

            val sido = "서울특별시" // TODO: ActivityViewModel에서 연결
            val gugun = null // TODO: ActivityViewModel에서 연결
            val dong = null // TODO: ActivityViewModel에서 연결

            mapViewModel.loadFacilities(
                sido = sido,
                gugun = gugun,
                dong = dong,
                category = category
            ){ success ->
                if (success){
                    val items = mapViewModel.facilityList.value


                    val filteredData = items
                        .asSequence()
                        .filter { item ->
                            // 지역 필터
                            (sido == null || item.sido_name == sido) &&
                            (gugun == null || item.gugun_name == gugun) &&
                            (dong == null || item.dong_name == dong) &&
                            (category == null || item.ctg3_name == category)
                        }.toList()

                    showFacilityMarkers(filteredData)

                    val results = filteredData
                        .map { item ->
                            val (openInt, closeInt) = parseOperatingHours(item.weekday_oper_time)
                            val now = getCurrentTimeInt()
                            val isOpen = now in openInt until closeInt

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
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
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
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBottomSheet(){
        behavior.isFitToContents = false
        behavior.halfExpandedRatio = 0.45f
        behavior.isHideable = true
        behavior.isDraggable = true
        behavior.peekHeight = 150.dp

//        var initY = 0f
//        var lastY = 0f

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        googleMap.clear()
                        binding.chipGroup.clearCheck()
                        mapViewModel.setCurrentChipType("")
                        binding.speedDial.visibility = View.VISIBLE
                        binding.fabToggleMylocation.visibility = View.VISIBLE
                        binding.fabToggleMylocationExpanded.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.speedDial.visibility = View.VISIBLE
                        binding.fabToggleMylocation.visibility = View.VISIBLE
                        binding.fabToggleMylocationExpanded.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        binding.speedDial.visibility = View.GONE
                        binding.fabToggleMylocation.visibility = View.GONE
                        binding.fabToggleMylocationExpanded.visibility = View.VISIBLE
                    }
                }
            }


            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

//        binding.viewHomeIndicator.bottomSheetHandle.setOnTouchListener { v, event ->
//            when (event.action){
//                MotionEvent.ACTION_DOWN ->{
//                    initY = event.rawY
//                    lastY = initY
//                    true
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    lastY = event.rawY
//                    true
//                }
//                MotionEvent.ACTION_UP -> {
//                    v.performClick()
//
//                    val deltaY = lastY - initY
//                    val state = behavior.state
//
//                    if(deltaY < -20){
//                        behavior.state = when(state){
//                            BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_HALF_EXPANDED
//                            BottomSheetBehavior.STATE_HALF_EXPANDED -> BottomSheetBehavior.STATE_EXPANDED
//                            else -> BottomSheetBehavior.STATE_EXPANDED
//                        }
//                    }else if(deltaY > 20){
//                        behavior.state = when(state){
//                            BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_HALF_EXPANDED
//                            BottomSheetBehavior.STATE_HALF_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
//                            else -> BottomSheetBehavior.STATE_COLLAPSED
//                        }
//                    }
//                    true
//                }else -> {false}
//            }
//        }

        binding.viewHomeIndicator.bottomSheetBehaviour.post{
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }
//
//
//    private fun showSearchResultFragment() {
//        childFragmentManager.beginTransaction()
//            .show(placeSearchFragment)
//            .hide(placeDetailsFragment)
//            .commit()
//    }
//
//    private fun showDetailsFragment() {
//        childFragmentManager.beginTransaction()
//            .show(placeDetailsFragment)
//            .hide(placeSearchFragment)
//            .commit()
//    }

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
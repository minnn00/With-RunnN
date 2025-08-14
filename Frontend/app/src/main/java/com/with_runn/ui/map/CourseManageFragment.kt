package com.with_runn.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.with_runn.ActivityViewModel
import com.with_runn.MainActivity
import com.with_runn.R
import com.with_runn.databinding.FragmentCourseManageBinding
import com.with_runn.dp
import com.with_runn.ui.course_edit.CourseData
import com.with_runn.ui.course_edit.CourseEditDialogFragment
import com.with_runn.ui.course_edit.PinEditDialogFragment
import com.with_runn.ui.course_edit.PinEditDialogFragment.Companion.Mode
import com.with_runn.ui.course_edit.PinItem
import com.with_runn.ui.course_edit.PinListAdapter
import kotlinx.coroutines.launch

class CourseManageFragment : Fragment() {

    private var _binding : FragmentCourseManageBinding? = null
    private val binding get() = _binding!!

    private val courseEditViewModel : CourseEditViewModel by viewModels()
    private val activityVM : ActivityViewModel by activityViewModels()

    private var isDragging = false

    private lateinit var itemTouchHelper : ItemTouchHelper

    private lateinit var googleMap: GoogleMap

    private lateinit var behavior : BottomSheetBehavior<View>

    private lateinit var pinListAdapter : PinListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        courseEditViewModel.setLocationPermission(checkLocationPermission())

        _binding = FragmentCourseManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityVM.setBottomNavVisibility(false)

        binding.mapView.apply {
            onCreate(savedInstanceState)

            getMapAsync {
                googleMap = it

                @SuppressLint("MissingPermission")
                if(courseEditViewModel.locationPermissionGranted.value){
                    moveToMyLocation()
                    googleMap.isMyLocationEnabled = true
                }else{
                    Log.e("PERMISSION ERROR", "LACK PERMISSION")
                    // TODO: 권한 재요청 후 승인 시 moveToMyLocation 수행
                }

                googleMap.apply{
                    setOnPoiClickListener { poi ->
                        if(courseEditViewModel.isPinning.value == true){
                            PinEditDialogFragment.newInstance(
                                PinItem(0, poi.name, "", poi.latLng.latitude, poi.latLng.longitude),
                                Mode.ADD
                            ).show(parentFragmentManager, "PinEditDialogFragment")
                        }else{
                            val marker = googleMap.addMarker(
                                MarkerOptions()
                                    .position(poi.latLng)
                                    .title(poi.name)
                            )
                            courseEditViewModel.setTempMarker(marker)
                            marker?.showInfoWindow()
                        }
                    }

                    setOnMapClickListener{ latLng ->
                        if(courseEditViewModel.isPinning.value == true){
                            PinEditDialogFragment.newInstance(
                                PinItem(0, "", "", latLng.latitude, latLng.longitude),
                                Mode.ADD
                            ).show(parentFragmentManager, "PinEditDialogFragment")
                        }else{
                            val marker = googleMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(latLng.toString())
                            )
                            courseEditViewModel.setTempMarker(marker)
                            marker?.showInfoWindow()
                        }

                    }

                    @SuppressLint("PotentialBehaviorOverride")
                    setOnMarkerClickListener{ marker ->
                        if(courseEditViewModel.isPinning.value == true){
                            val mode: Mode =
                                if(marker == courseEditViewModel.tempMarker.value) Mode.ADD
                                else Mode.EDIT

                            PinEditDialogFragment.newInstance(
                                PinItem(
                                    0,
                                    marker.title.toString(),
                                    marker.snippet?.toString() ?: "",
                                    marker.position.latitude,
                                    marker.position.longitude),
                                mode
                            ).show(parentFragmentManager, "PinEditDialogFragment")

                            courseEditViewModel.removeTempMarker()
                        }else{
                            marker.showInfoWindow()
                        }
                        false
                    }

                    uiSettings.apply {
                        isCompassEnabled = false
                        isMyLocationButtonEnabled = false
                        isMapToolbarEnabled = false
                    }
                }

                setCoroutines()
                courseEditViewModel.setSampleData()

                courseEditViewModel.askDirections()
            }
        }

        behavior = BottomSheetBehavior.from(binding.bottomSheetBehaviour)

        setupBottomSheet()
        setBackPressedCallback()
        setListeners()

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

        pinListAdapter = PinListAdapter(
            onItemClick = { item -> onClickItem(item) },
            onItemDrag = { vh -> itemTouchHelper.startDrag(vh) }
        )

        binding.pinListRcv.apply{
            adapter = pinListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        itemTouchHelper.attachToRecyclerView(binding.pinListRcv)
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

    private fun setCoroutines(){
        // PinList Collector
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                courseEditViewModel.pinList.collect { list ->
                    if(!isDragging){pinListAdapter.submitList(list)}

                    googleMap.clear()

                    list.forEach { pin ->
                        val markerOption = MarkerOptions()
                            .position(LatLng(pin.lat, pin.lng))
                            .title(pin.name)

                        if(pin.content != ""){
                            markerOption.snippet(pin.content)
                        }
                        googleMap.addMarker(markerOption)
                    }

                    val isPinExist = list.isNotEmpty()
                    if(isPinExist){
                        if(behavior.state == BottomSheetBehavior.STATE_HIDDEN){
                            binding.bottomSheetBehaviour.visibility = View.INVISIBLE
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            binding.bottomSheetBehaviour.visibility = View.VISIBLE
                            behavior.isHideable = false
                        }
                    }else{
                        behavior.isHideable = true
                        behavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }
        }

        // isPinning Collector
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                courseEditViewModel.isPinning.collect { isPinning ->
                    if(isPinning){
                        courseEditViewModel.removeTempMarker()
                    }

                    val context = requireContext()

                    val iconTint = ContextCompat.getColorStateList(
                        context,
                        if (isPinning) R.color.gray_050 else R.color.green_700
                    )

                    val bgTint = ContextCompat.getColorStateList(
                        context,
                        if (isPinning) R.color.green_700 else R.color.gray_050
                    )

                    val visibility = if(isPinning) View.VISIBLE else View.GONE

                    binding.apply{
                        createPinFab.imageTintList = iconTint
                        createPinFab.backgroundTintList = bgTint

                        pinAddingAlert.visibility = visibility
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                courseEditViewModel.polyLineData.collect{ directions ->
                    courseEditViewModel.removePolyline()

                    val polyline = googleMap.addPolyline(
                        PolylineOptions()
                            .addAll(directions)
                            .color(Color.BLUE)
                            .width(10f)
                    )

                    courseEditViewModel.setPolyline(polyline)

                    val enabled = !directions.isEmpty()
                        binding.apply{
                            btnSave.isEnabled = enabled
                            btnSave.setBackgroundResource(
                                if (enabled) R.drawable.bg_btn_filled else R.drawable.bg_btn_filled_inactive
                            )
                        }
                }
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupBottomSheet(){
        behavior.apply{
            peekHeight = 150.dp
            isHideable = courseEditViewModel.pinList.value.isEmpty()
            isFitToContents = true
            halfExpandedRatio = 0.55f
        }

        binding.bottomSheetBehaviour.post{
            behavior.isHideable = courseEditViewModel.pinList.value.isEmpty()
            if(courseEditViewModel.pinList.value.isEmpty()){
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }else{
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

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

                        else -> {
                            isEnabled = false
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            })
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setListeners(){
        binding.apply {
            createPinFab.setOnClickListener {
                courseEditViewModel.togglePinningBtn()
            }

            @SuppressLint("MissingPermission")
            toMyLocationFab.setOnClickListener{
                moveToMyLocation()
            }

            btnCreate.setOnClickListener {
                val size = courseEditViewModel.pinList.value.size

                if(size > 1){
                    courseEditViewModel.askDirections()
                }
            }

            btnSave.setOnClickListener {
                CourseEditDialogFragment.newInstance(
                    courseEditViewModel.courseData.value
                ).show(parentFragmentManager, "PinEditDialogFragment")
            }
        }


        parentFragmentManager.setFragmentResultListener("pin_edit_result", viewLifecycleOwner) { key, bundle ->
            val pin = bundle.getParcelable<PinItem>("pin_item", PinItem::class.java) ?: error("Pin argument required")
            val mode = bundle.getSerializable<Mode>("mode", Mode::class.java) ?: error("Mode argument required")

            if(mode == Mode.ADD){
                courseEditViewModel.addPin(pin)
            }else if(mode == Mode.EDIT){
                courseEditViewModel.updatePin(pin)
            }

        }

        parentFragmentManager.setFragmentResultListener("course_edit_result", viewLifecycleOwner) { key, bundle ->
            val course = bundle.getParcelable<CourseData>("course_data", CourseData::class.java) ?: error("CourseData argument required")

            // TODO: Course Post
            // TODO: 상세보기로 이동 (백스택 남기지 않고)ㄹ
        }
    }

    private fun onClickItem(item: PinItem){
        val latLng = LatLng(item.lat, item.lng)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
    ){
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                isDragging = true
            }
            if(actionState == ItemTouchHelper.ACTION_STATE_IDLE){
                if (isDragging) {
                    isDragging = false
                    courseEditViewModel.setPinList(pinListAdapter.getCurrentList())
                }
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.bindingAdapterPosition
            val to = target.bindingAdapterPosition

            pinListAdapter.swapItem(from, to)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            val removedItem = pinListAdapter.removeItem(position)
            val prevList = courseEditViewModel.pinList.value.toList() // ViewModel의 원본 상태

            courseEditViewModel.setPinList(pinListAdapter.getCurrentList())

            (activity as? MainActivity)?.showSnackbar(
                message = "${removedItem.name} 삭제됨",
                actionText = "UNDO"
            ) {
                courseEditViewModel.setPinList(prevList)
            }
        }
        override fun isLongPressDragEnabled() = false
    }
}
package com.with_runn.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
import androidx.core.graphics.toColorInt
import androidx.core.os.BundleCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.with_runn.data.TokenManager
import com.with_runn.data.course.CourseManageArgs
import com.with_runn.data.course.CourseMode
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlin.math.min
import kotlin.math.roundToInt
import androidx.core.net.toUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okio.buffer
import okio.source
import java.net.URLConnection
import java.io.File

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityVM.setBottomNavVisibility(false)
        activityVM.setUpperToolbarVisibility(false)


        val args: CourseManageArgs? =
            if (Build.VERSION.SDK_INT >= 33)
                requireArguments().getSerializable("args", CourseManageArgs::class.java)
            else
                @Suppress("DEPRECATION")
                requireArguments().getSerializable("args") as CourseManageArgs
        courseEditViewModel.setMode(args?.mode ?: CourseMode.CREATE, args?.courseId)

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
                                    .icon(resToMarkerIcon(R.drawable.ic_basic_pin))
                            )

                            courseEditViewModel.setTempMarker(marker)
                            marker?.showInfoWindow()
                        }
                    }

                    setOnMapClickListener{ latLng ->
//                        if(courseEditViewModel.isPinning.value == true){
//                            PinEditDialogFragment.newInstance(
//                                PinItem(0, "", "", latLng.latitude, latLng.longitude),
//                                Mode.ADD
//                            ).show(parentFragmentManager, "PinEditDialogFragment")
//                        }else{
//                            val marker = googleMap.addMarker(
//                                MarkerOptions()
//                                    .position(latLng)
//                                    .title(latLng.toString())
//                                    .icon(resToMarkerIcon(R.drawable.ic_basic_pin))
//                            )
//                            courseEditViewModel.setTempMarker(marker)
//                            marker?.showInfoWindow()
//                        }

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

                if (courseEditViewModel.mode.value == CourseMode.EDIT) {
                    val token = TokenManager.getAccessToken().orEmpty()
                    val id = args?.courseId ?: error("EDIT mode requires courseId")
                    courseEditViewModel.loadCourseDetail(token, id)
                }
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
                            .icon(resToMarkerIcon(R.drawable.ic_basic_pin))

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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                courseEditViewModel.polyLineData.collect { directions ->
                    // 1) 이전 라인 제거 (메인 + 외곽)
                    courseEditViewModel.polyLine.value?.let { prevMain ->
                        (prevMain.tag as? com.google.android.gms.maps.model.Polyline)?.remove() // outline
                        prevMain.remove() // main
                    }
                    courseEditViewModel.removePolyline()

                    if (directions.isEmpty()) {
                        binding.btnSave.isEnabled = false
                        binding.btnSave.setBackgroundResource(R.drawable.bg_btn_filled_inactive)
                        return@collect
                    }

                    // 2) 외곽선(underlay)
                    val outline = googleMap.addPolyline(
                        PolylineOptions()
                            .addAll(directions)
                            .width(20f)
                            .color(Color.argb(80, 0, 0, 0)) // 반투명 짙은 회색
                            .zIndex(0f)
                            .startCap(com.google.android.gms.maps.model.RoundCap())
                            .endCap(com.google.android.gms.maps.model.RoundCap())
                            .jointType(com.google.android.gms.maps.model.JointType.ROUND)
                    )

                    // 3) 본선(overlay)
                    val main = googleMap.addPolyline(
                        PolylineOptions()
                            .addAll(directions)
                            .width(7f)
                            .color("#2F7CF6".toColorInt()) // 메인 컬러
                            .zIndex(1f)
                            .startCap(com.google.android.gms.maps.model.RoundCap())
                            .endCap(com.google.android.gms.maps.model.RoundCap())
                            .jointType(com.google.android.gms.maps.model.JointType.ROUND)
                    )

                    // 4) ViewModel에는 "메인"만 저장하고, 외곽은 tag로 함께 보관 → 다음 업데이트 때 같이 제거
                    main.tag = outline
                    courseEditViewModel.setPolyline(main)

                    // 5) 버튼 상태
                    val enabled = directions.isNotEmpty()
                    binding.apply {
                        btnSave.isEnabled = enabled
                        btnSave.setBackgroundResource(
                            if (enabled) R.drawable.bg_btn_filled else R.drawable.bg_btn_filled_inactive
                        )
                    }
                }
            }
        }

        // Detail 로드 성공 시 한 번만 카메라 맞추기
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                courseEditViewModel.detailState.collect { state ->
                    when (state) {
                        is CourseEditViewModel.DetailUiState.Success -> {
                            // 1) 카메라 핏 (경로 > 핀)
                            val path = courseEditViewModel.polyLineData.value
                            if (path.isNotEmpty()) {
                                fitCameraTo(path, paddingDp = 64)
                            } else {
                                val pins = courseEditViewModel.pinList.value
                                if (pins.isNotEmpty()) fitCameraTo(pins.map { LatLng(it.lat, it.lng) }, paddingDp = 48)
                            }

                            // 2) 바텀시트 확장 (로드 성공 시 한 번 강제 오픈)
                            val hasPins = courseEditViewModel.pinList.value.isNotEmpty()
                            if (hasPins) {
                                binding.bottomSheetBehaviour.post {
                                    behavior.isHideable = false
                                    // 숨겨져 있었다면 먼저 보이게
                                    if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                                        binding.bottomSheetBehaviour.visibility = View.VISIBLE
                                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                    }
                                    // 최종: 확장
                                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                                }
                            }
                        }
                        else -> Unit
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
            val pin  = BundleCompat.getParcelable(bundle, "pin_item", PinItem::class.java)
                ?: error("Pin argument required")

            val mode = BundleCompat.getSerializable(bundle, "mode", Mode::class.java)
                ?: error("Mode argument required")

            if(mode == Mode.ADD){
                courseEditViewModel.addPin(pin)
            }else if(mode == Mode.EDIT){
                courseEditViewModel.updatePin(pin)
            }

        }

        parentFragmentManager.setFragmentResultListener("course_edit_result", viewLifecycleOwner) { key, bundle ->
            val course = BundleCompat.getParcelable(bundle, "course_data", CourseData::class.java)
                ?: error("CourseData argument required")

            // 저장 버튼 잠금
            binding.btnSave.isEnabled = false

            courseEditViewModel.overrideCourseMeta(course)

            // ViewModel의 필드/ActivityVM에서 파라미터 모으기
            val accessToken = TokenManager.getAccessToken().toString()// raw or "Bearer ..."
            val townId = activityVM.thirdRegion.value?.id
            val provinceId = activityVM.firstRegion.value.id
            val cityId = activityVM.secondRegion.value?.id
            val imagePart = buildImagePart(course.imageUrl)

            val keywords = course.keyword?.takeIf { it.isNotBlank() }?.let { listOf(it) } ?: emptyList()
            Log.e("KEYWORDS", "$keywords")

            when (courseEditViewModel.mode.value) {
                CourseMode.CREATE -> {
                    // ViewModel 통해 호출
                    courseEditViewModel.postCourse(
                        accessToken = accessToken,
                        keywords = keywords,
                        townId = townId,
                        provinceId = provinceId ?: 9,
                        cityId = cityId,
                        imagePart = imagePart
                    ) { success, createdId ->
                        if (success && createdId != null) {
                            val bundle = Bundle().apply {
                                putInt("courseId", createdId)
                            }
                            findNavController().navigate(R.id.courseDetailFragment, bundle)
                        } else {
                            (activity as? MainActivity)?.showSnackbar("코스 생성 실패 또는 ID 누락")
                            binding.btnSave.isEnabled = true
                        }
                    }
                }
                CourseMode.EDIT -> {
                    // PATCH 호출. 아직 API 미구현 → ViewModel의 updateCourse 자리만 확보
                    val editingId = courseEditViewModel.loadedCourseId.value ?: run {
                        (activity as? MainActivity)?.showSnackbar("코스 ID 누락")
                        binding.btnSave.isEnabled = true
                        return@setFragmentResultListener
                    }
                    courseEditViewModel.updateCourse(
                        accessToken = accessToken,
                        courseId = editingId,
                        keywords = keywords,
                        townId = townId,
                        provinceId = provinceId ?: 9,
                        cityId = cityId
                    ) { success ->
                        if (success) {
                            (activity as? MainActivity)?.showSnackbar("코스가 업데이트되었습니다")
                            findNavController().navigate(
                                R.id.courseDetailFragment,
                                Bundle().apply { putInt("courseId", editingId) }
                            )
                        } else {
                            (activity as? MainActivity)?.showSnackbar("코스 수정 실패")
                            binding.btnSave.isEnabled = true
                        }
                    }
                }
            }

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

    fun resToMarkerIcon(resId: Int, maxSizeDp: Float? = null): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(requireContext(), resId) ?: error("Resource not found")
        val dm = requireContext().resources.displayMetrics
        val density = dm.density

        val intrinsicW = drawable.intrinsicWidth.takeIf { it > 0 } ?: (24 * density).toInt()
        val intrinsicH = drawable.intrinsicHeight.takeIf { it > 0 } ?: (24 * density).toInt()

        val (outW, outH) = if (maxSizeDp == null) {
            intrinsicW to intrinsicH
        } else {
            val maxPx = (maxSizeDp * density).toInt().coerceAtLeast(1)
            val ratio = min(maxPx / intrinsicW.toFloat(), maxPx / intrinsicH.toFloat())
            (intrinsicW * ratio).roundToInt().coerceAtLeast(1) to
                    (intrinsicH * ratio).roundToInt().coerceAtLeast(1)
        }

        val bitmap = drawable.toBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun fitCameraTo(points: List<LatLng>, paddingDp: Int = 48) {
        if (points.isEmpty()) return
        val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
        points.forEach { builder.include(it) }
        val bounds = builder.build()
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, paddingDp.dp)
        )
    }

    private fun buildImagePart(uriString: String?): MultipartBody.Part? {
        if (uriString.isNullOrBlank()) return null
        val uri = uriString.toUri()

        return when (uri.scheme?.lowercase()) {
            "content" -> {
                val cr = requireContext().contentResolver
                val mime = cr.getType(uri) ?: "image/*"
                // 파일명
                val name = cr.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
                    ?.use { c -> if (c.moveToFirst()) c.getString(0) else null } ?: "image_${System.currentTimeMillis()}"
                // 스트리밍 RequestBody (임시파일 없이 업로드)
                val rb = object : RequestBody() {
                    override fun contentType() = mime.toMediaTypeOrNull()
                    override fun writeTo(sink: okio.BufferedSink) {
                        cr.openInputStream(uri)!!.use { input ->
                            sink.writeAll(input.source().buffer())
                        }
                    }
                }
                MultipartBody.Part.createFormData("courseImg", name, rb)
            }
            "file" -> {
                val file = File(requireNotNull(uri.path))
                val mime = URLConnection.guessContentTypeFromName(file.name) ?: "image/*"
                val rb = file.asRequestBody(mime.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("courseImg", file.name, rb)
            }
            else -> {
                // 스킴이 없고 그냥 절대경로인 경우
                val file = File(uriString)
                if (!file.exists()) return null
                val mime = URLConnection.guessContentTypeFromName(file.name) ?: "image/*"
                val rb = file.asRequestBody(mime.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("courseImg", file.name, rb)
            }
        }
    }
}
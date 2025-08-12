package com.with_runn.ui.course

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.with_runn.ActivityViewModel
import com.with_runn.R
import com.with_runn.data.course.CourseFetchRepository
import com.with_runn.data.course.CourseService
import com.with_runn.data.viewmodel.CourseDetailsVMFactory
import com.with_runn.data.viewmodel.CourseDetailsViewModel
import com.with_runn.dp
import kotlinx.coroutines.launch
import kotlin.getValue
import androidx.fragment.app.Fragment
import com.with_runn.databinding.FragmentCourseDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.with_runn.data.course.CourseDetailResponse
import com.with_runn.ui.course_edit.PinItem
import androidx.core.graphics.toColorInt
import com.with_runn.data.course.CourseActionRepository
import com.with_runn.toHM
import kotlin.math.min
import kotlin.math.roundToInt

class CourseDetailFragment : Fragment() {

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!
    private val activityVM : ActivityViewModel by activityViewModels()
    private lateinit var courseDetailsVM: CourseDetailsViewModel

    private var isMapReady = false
    private var rendered = false
    private var cachedCourse: CourseDetailResponse? = null

    private var routeMain: Polyline? = null
    private val markerToPin = mutableMapOf<Marker, PinItem>()

    private lateinit var behavior : BottomSheetBehavior<View>
    private lateinit var googleMap: GoogleMap

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val courseId = arguments?.getInt("courseId") ?: throw IllegalStateException("courseId is required for CourseDetailFragment")

        val token = activityVM.accessToken.value.orEmpty()
        val repository = CourseFetchRepository(CourseService.api)
        val actionRepo = CourseActionRepository(CourseService.actionApi)
        courseDetailsVM = CourseDetailsVMFactory(token, repository, actionRepo)
            .create(CourseDetailsViewModel::class.java)

        courseDetailsVM.fetchCourse(courseId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailBinding.inflate(inflater, container, false)
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


                googleMap.apply{


                    @SuppressLint("PotentialBehaviorOverride")
                    setOnMarkerClickListener{ marker ->
                        val pin = markerToPin[marker]

                        val args = Bundle().apply {
                            putString("pin_name", pin?.name ?: marker.title)
                            putString("pin_detail", pin?.content ?: marker.snippet)
                        }

                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(marker.position, 16f),
                            object : GoogleMap.CancelableCallback {
                                override fun onFinish() {
                                    PinInfoDialogFragment
                                        .newInstance(args)
                                        .show(parentFragmentManager, "PinInfo")
                                }
                                override fun onCancel() { /* 필요시 처리 */ }
                            }
                        )
                        true
                    }

                    uiSettings.apply {
                        isCompassEnabled = false
                        isMyLocationButtonEnabled = false
                        isMapToolbarEnabled = false
                    }

                    isMapReady = true
                    cachedCourse?.let { if (!rendered) renderCourseOnMap(it) }
                }

            }
        }

        behavior = BottomSheetBehavior.from(binding.bottomSheetBehaviour)
        setupBottomSheet()
        setListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    courseDetailsVM.isLiked.collect { liked ->
                        binding.likeIc.setImageResource(
                            if (liked) R.drawable.ic_heart_active else R.drawable.ic_heart_outlined
                        )
                    }
                }
                launch {
                    courseDetailsVM.isBookmarked.collect { bookmarked ->
                        binding.bookmarkIc.setImageResource(
                            if (bookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outlined
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                courseDetailsVM.courseData.collect { course ->
                    if (rendered) return@collect


                    binding.apply {
                        courseName.text = course.name
                        courseInfo.text = course.description
                        estimatedTime.text = course.time.toHM()
                        setTags(course.keywords)

                        Glide.with(requireContext())
                            .load(course.imageUrl)
                            .error(R.drawable.img_app_logo)
                            .into(courseImage)
                    }

                    cachedCourse = course

                    if (isMapReady) {
                        renderCourseOnMap(course)
                    }
                }
            }
        }

    }

    private fun setupBottomSheet(){
        behavior.apply{
            isHideable = false
            isFitToContents = true
        }

        binding.bottomSheetBehaviour.post{
            val handleHeight = binding.bottomSheetHandle.height
            val infoHeight = binding.courseSimpleInfo.height
            val extraPadding = 36.dp

            val totalPeekHeight = handleHeight + infoHeight + extraPadding
            behavior.apply{
                peekHeight = totalPeekHeight
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setTags(tags: List<String>){
        binding.tagContainer.apply{
            removeAllViews()
            tags.take(1).forEach { tag ->
                val tagView = layoutInflater.inflate(R.layout.item_tag, this, false) as TextView
                tagView.text = "#" + tag
                addView(tagView)
            }
        }
    }

    private fun setListeners(){
        binding.apply {
            backBtn.setOnClickListener { findNavController().popBackStack() }
            btnLike.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = courseDetailsVM.toggleLike()()
                    if (!result) {
                        showToast("좋아요 처리 실패")
                    }
                }
            }
            btnScrap.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = courseDetailsVM.toggleScrap()()
                    if (!result) {
                        showToast("북마크 처리 실패")
                    }
                }
            }
            btnShare
        }
    }

    private fun renderCourseOnMap(course: CourseDetailResponse) {
        // 안전 제거
        googleMap.clear()
        routeMain?.let { main ->
            (main.tag as? Polyline)?.remove()
            main.remove()
        }
        routeMain = null
        markerToPin.clear()

        // 핀 생성
        course.pins.forEach { p ->
            val m = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(p.lat, p.lng))
                    .title(p.name)
                    .snippet(p.content)
                    .icon(resToMarkerIcon(R.drawable.ic_basic_pin))
            )
            if (m != null) markerToPin[m] = p
        }

        // 폴리라인(있을 때만)
        val path = decodeOverviewPolyline(course.overviewPolyline)
        if (path.isNotEmpty()) {
            val outline = googleMap.addPolyline(
                PolylineOptions()
                    .addAll(path)
                    .width(20f)
                    .color(Color.argb(80, 0, 0, 0))
                    .zIndex(0f)
                    .startCap(RoundCap())
                    .endCap(RoundCap())
                    .jointType(JointType.ROUND)
            )
            val main = googleMap.addPolyline(
                PolylineOptions()
                    .addAll(path)
                    .width(7f)
                    .color("#2F7CF6".toColorInt())
                    .zIndex(1f)
                    .startCap(RoundCap())
                    .endCap(RoundCap())
                    .jointType(JointType.ROUND)
            )
            main.tag = outline
            routeMain = main
            fitCameraToPath(path)
        } else {
            fitCameraToPins(course.pins)
        }

        rendered = true
    }

    // 유틸들
    private fun decodeOverviewPolyline(encoded: String?): List<LatLng> {
        if (encoded.isNullOrBlank()) return emptyList()
        return try { PolyUtil.decode(encoded) } catch (_: Exception) { emptyList() }
    }

    private fun fitCameraToPath(path: List<LatLng>) {
        if (path.isEmpty()) return
        val b = LatLngBounds.Builder()
        path.forEach { b.include(it) }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 80))
    }

    private fun fitCameraToPins(pins: List<PinItem>) {
        if (pins.isEmpty()) return
        val b = LatLngBounds.Builder()
        pins.forEach { b.include(LatLng(it.lat, it.lng)) }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 80))
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
        routeMain = null
        markerToPin.clear()
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

package com.with_runn.ui.course

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.with_runn.databinding.FragmentCourseDetailBinding
import android.widget.TextView
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.with_runn.ActivityViewModel
import com.with_runn.data.WalkCourse
import com.with_runn.R
import com.with_runn.data.course.CourseFetchRepository
import com.with_runn.data.course.CourseService
import com.with_runn.data.viewmodel.CourseDetailsVMFactory
import com.with_runn.data.viewmodel.CourseDetailsViewModel
import com.with_runn.dp
import com.with_runn.ui.course_edit.PinEditDialogFragment
import com.with_runn.ui.course_edit.PinEditDialogFragment.Companion.Mode
import com.with_runn.ui.course_edit.PinItem
import kotlinx.coroutines.launch
import kotlin.getValue


class CourseDetailFragment : Fragment() {

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!

    private val activityVM : ActivityViewModel by activityViewModels()
    private lateinit var courseDetailsVM: CourseDetailsViewModel

    private lateinit var behavior : BottomSheetBehavior<View>
    private lateinit var googleMap: GoogleMap

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [1] Argument에서 courseId만 받기
        val courseId = arguments?.getInt("courseId") ?: throw IllegalStateException("courseId is required for CourseDetailFragment")

        // [2] ViewModel 생성 및 fetchCourse
        val token = activityVM.accessToken.value.orEmpty()
        val repository = CourseFetchRepository(CourseService.api)
        courseDetailsVM = CourseDetailsVMFactory(token, repository)
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
                    setOnPoiClickListener { poi ->
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(poi.latLng)
                                .title(poi.name)
                        )
                        courseDetailsVM.setTempMarker(marker)
                        marker?.showInfoWindow()
                    }

                    uiSettings.apply {
                        isCompassEnabled = false
                        isMyLocationButtonEnabled = false
                        isMapToolbarEnabled = false
                    }
                }
            }
        }

        behavior = BottomSheetBehavior.from(binding.bottomSheetBehaviour)
        setupBottomSheet()
        setListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                courseDetailsVM.courseData.collect { course ->
                    binding.apply {
                        courseName.text = course.name
                        courseInfo.text = course.description
                        estimatedTime.text = course.time
                        setTags(course.keywords)

                        Glide.with(requireContext())
                            .load(course.imageUrl)
                            .error(R.drawable.img_app_logo)
                            .into(courseImage)
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
            tags.take(2).forEach { tag ->
                val tagView = layoutInflater.inflate(R.layout.item_tag, this, false) as TextView
                tagView.text = "#" + tag
                addView(tagView)
            }
        }
    }

    private fun setListeners(){
        binding.apply {
            backBtn.setOnClickListener { findNavController().popBackStack() }
        }
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
}

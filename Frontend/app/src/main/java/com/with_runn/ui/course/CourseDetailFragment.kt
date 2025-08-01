package com.with_runn.ui.course

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
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
/*
    private lateinit var viewModel: CourseDetailViewModel
    private lateinit var likeViewModel: WalkCourseViewModel
    private lateinit var repository: CourseRepository

    private var isScrapped: Boolean = false
*/

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
/*
        Log.d("CourseDetail", "onViewCreated 호출됨")

        val courseId = arguments?.getInt("courseId") ?: return
        Log.d("CourseDetail", "받은 courseId: $courseId")

        repository = CourseRepository()
        val factory = CourseDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CourseDetailViewModel::class.java]
        likeViewModel = ViewModelProvider(this)[WalkCourseViewModel::class.java]

        // 코스 상세 데이터 요청
        viewModel.fetchCourseDetail(courseId)

        // 코스 상세정보 LiveData 관찰
        viewModel.courseDetail.observe(viewLifecycleOwner) { course ->
            Log.d("CourseDetail", "courseDetail observe 호출됨")

            // UI에 기본 정보 바인딩
            Glide.with(this).load(course.imageUrl).into(binding.imageCourse)
            binding.textTitle.text = course.name
            binding.textDescription.text = "우리 동네 코스 소개\n${course.time} 소요됩니다."
            binding.textTimeValue.text = course.time.replace("분", "M")

            // 태그 동적 생성
            val tagContainer = binding.layoutTags
            tagContainer.removeAllViews()
            val inflater = LayoutInflater.from(requireContext())
            course.keywords.take(2).forEach { tag ->
                val tagView = inflater.inflate(R.layout.item_tag, tagContainer, false) as TextView
                tagView.text = tag
                tagContainer.addView(tagView)
            }

            // 초기 스크랩 상태 저장
            isScrapped = course.isScrapped

            // 좋아요 버튼 클릭 이벤트 처리
            binding.btnLike.setOnClickListener {
                Log.d("Test", "좋아요 버튼 클릭됨")
                likeViewModel.postLike(course.id)
                findNavController().navigate(R.id.action_courseDetailFragment_to_mypage_graph)
            }

            // 스크랩 버튼 클릭 이벤트 처리
            binding.btnScrap.setOnClickListener {
                Log.d("CourseDetail", "스크랩 버튼 클릭됨: ${course.name}")
                val userId = 1 // 임시 사용자 ID

                lifecycleScope.launch {
                    try {
                        val response = if (!isScrapped) {
                            repository.postScrap(ScrapRequest(userId, course.id))
                        } else {
                            repository.deleteScrap(course.id)
                        }

                        if (response.isSuccessful) {
                            val message = response.body()?.message
                            Log.d("Scrap", "성공: $message")
                            isScrapped = !isScrapped
                        } else {
                            Log.e("Scrap", "실패: ${response.errorBody()?.string()}")
                        }

                    } catch (e: Exception) {
                        Log.e("Scrap", "예외 발생: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }

            // 공유 버튼 클릭 이벤트 처리
            binding.btnShare.setOnClickListener {
                Log.d("CourseDetail", "공유 버튼 클릭됨")
                // TODO: 공유 바텀시트 연결 예정

            }
        }
    }
*/
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

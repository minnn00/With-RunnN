package com.with_runn.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.ScrapRequest
import com.with_runn.data.repository.CourseRepository
import com.with_runn.data.viewmodel.CourseDetailViewModel
import com.with_runn.data.viewmodel.CourseDetailViewModelFactory
import com.with_runn.data.viewmodel.WalkCourseViewModel
import com.with_runn.databinding.FragmentCourseDetailBinding
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController


class CourseDetailFragment : Fragment() {

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CourseDetailViewModel
    private lateinit var likeViewModel: WalkCourseViewModel
    private lateinit var repository: CourseRepository

    private var isScrapped: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

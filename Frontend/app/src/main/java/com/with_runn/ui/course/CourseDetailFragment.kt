package com.with_runn.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.repository.CourseRepository
import com.with_runn.data.ShareRequest
import com.with_runn.data.viewmodel.CourseDetailViewModel
import com.with_runn.data.viewmodel.CourseDetailViewModelFactory
import com.with_runn.databinding.FragmentCourseDetailBinding

class CourseDetailFragment : Fragment() {

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CourseDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val courseId = arguments?.getInt("courseId") ?: return

        val repository = CourseRepository()
        val factory = CourseDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CourseDetailViewModel::class.java]

        // 상세 데이터 요청
        viewModel.fetchCourseDetail(courseId)

        // 상세 데이터 observe → UI 바인딩
        viewModel.courseDetail.observe(viewLifecycleOwner) { course ->
            course?.let {
                Glide.with(this).load(it.imageUrl).into(binding.imageCourse)
                binding.textTitle.text = it.name
                binding.textDescription.text = "우리 동네 코스 소개\n${it.time} 소요됩니다."
                binding.textTimeValue.text = it.time.replace("분", "M")
                // ... 태그 등 나머지 UI 세팅
            }
        }

        // 좋아요 버튼 리스너 (ViewModel 통해 API 호출)
        binding.btnLike.setOnClickListener {
            Log.d("LikeBtn", "좋아요 버튼 클릭됨 (courseId=$courseId)")
            viewModel.postLike(courseId)
        }

        // 좋아요 결과 메시지 observe → Toast 등으로 안내
        viewModel.likeMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // 스크랩 버튼 리스너 (ViewModel 통해 API 호출)
        binding.btnScrap.setOnClickListener {
            Log.d("ScrapBtn", "스크랩 버튼 클릭됨 (courseId=$courseId)")
            viewModel.postScrap(courseId) { msg ->
                Log.d("ScrapResult", "스크랩 API 결과: $msg")
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        // 공유 버튼 리스너 (ViewModel 통해 API 호출)
        binding.btnShare.setOnClickListener {
            Log.d("ShareBtn", "공유 버튼 클릭됨 (courseId=$courseId)")
            val isChat = true
            val userId = 1
            val targetUserId = null     // 채팅방 공유면 null
            val chatId = 1              // 실제 채팅방 id
            viewModel.postShareCourse(
                isChat = isChat,
                userId = userId,
                targetUserId = targetUserId,
                chatId = chatId,
                courseId = courseId
            ) { success, msg ->
                if (success) {
                    Log.d("ShareResult", "공유 성공: $msg")
                    Toast.makeText(requireContext(), "공유 성공: $msg", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ShareResult", "공유 실패: $msg")
                    Toast.makeText(requireContext(), "공유 실패: $msg", Toast.LENGTH_SHORT).show()
                }
            }
        }

//        binding.btnScrap.setOnClickListener {
//            Log.d("DeleteScrapBtn", "스크랩 취소 버튼 클릭 (courseId=$courseId)")
//            viewModel.deleteScrap(courseId) { msg ->
//                Log.d("DeleteScrapResult", "스크랩 취소 결과: $msg")
//                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

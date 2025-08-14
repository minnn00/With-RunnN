package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.with_runn.data.LikeRequest
import com.with_runn.data.NeighborhoodPreviewResponse
import com.with_runn.data.RisingPreviewResponse
import com.with_runn.data.WalkCourse
import com.with_runn.data.repository.CourseRepository
import kotlinx.coroutines.launch
import com.with_runn.data.toWalkCourse


class WalkCourseViewModel : ViewModel() {

    private val repository = CourseRepository()

    private val _neighborhoodCourses = MutableLiveData<List<WalkCourse>>()
    val neighborhoodCourses: LiveData<List<WalkCourse>> = _neighborhoodCourses

    private val _risingCourses = MutableLiveData<List<WalkCourse>>()
    val risingCourses: LiveData<List<WalkCourse>> = _risingCourses

    private val _neighborhoodPreview = MutableLiveData<List<NeighborhoodPreviewResponse>>()
    val neighborhoodPreview: LiveData<List<NeighborhoodPreviewResponse>> = _neighborhoodPreview

    private val _risingPreview = MutableLiveData<List<RisingPreviewResponse>>()
    val risingPreview: LiveData<List<RisingPreviewResponse>> = _risingPreview

    fun fetchNeighborhoodPreview(provinceId: Int, cityId: Int? = null, townId: Int? = null) {
        viewModelScope.launch {
            try {
                val response = repository.getNeighborhoodPreview(provinceId, cityId, townId)
                if (response.isSuccessful) {
                    Log.d("WalkCourseVM", "우리동네 산책코스 API 성공: ${response.body()}")
                    _neighborhoodPreview.value = response.body() ?: emptyList()
                } else {
                    Log.e(
                        "WalkCourseVM",
                        "우리동네 산책코스 API 실패: ${response.code()} / ${response.errorBody()?.string()}"
                    )
                    _neighborhoodPreview.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("WalkCourseVM", "우리동네 산책코스 API 예외: ${e.message}")
                e.printStackTrace()
                _neighborhoodPreview.value = emptyList()
            }
        }
    }


    fun fetchRisingPreview() {
        viewModelScope.launch {
            val result = repository.getRisingPreview()
            Log.d("WalkCourseVM", "떠오르는 코스 미리보기 API 응답: $result")
            _risingPreview.value = result ?: emptyList()
        }
    }

    fun loadDummyCourses() {
        _neighborhoodCourses.value = listOf(
            WalkCourse(
                id = 1,
                title = "강아지와 한강 산책",
                tags = listOf("#한강", "#강아지", "#산책"),
                imageResId = com.with_runn.R.drawable.image,
                distance = "2.5km",
                time = "33분",
                isScrapped = false,
                isLiked = false,
                imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb"
            ),
            // ... 더 추가 가능!
        )
    }

}

package com.with_runn.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.with_runn.data.LikeRequest
import com.with_runn.data.WalkCourseResponse
import com.with_runn.data.repository.CourseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalkCourseViewModel : ViewModel() {

    private val repository = CourseRepository()

    private val _neighborhoodCourses = MutableLiveData<List<WalkCourseResponse>>()
    val neighborhoodCourses: LiveData<List<WalkCourseResponse>> = _neighborhoodCourses

    private val _risingCourses = MutableLiveData<List<WalkCourseResponse>>()
    val risingCourses: LiveData<List<WalkCourseResponse>> = _risingCourses

    fun fetchNeighborhoodCourses() {
        viewModelScope.launch {
            delay(300)
            _neighborhoodCourses.value = listOf(
                WalkCourseResponse(
                    id = 1,
                    title = "망원한강공원",
                    imageUrl = "https://example.com/image1.jpg",
                    tags = listOf("#초보자추천"),
                    distanceMeters = 2000,
                    durationMinutes = 30,
                    isScrapped = false,
                    isLiked = false
                ),
                WalkCourseResponse(
                    id = 2,
                    title = "연남동 코스",
                    imageUrl = "https://example.com/image2.jpg",
                    tags = listOf("#풍경좋음"),
                    distanceMeters = 1800,
                    durationMinutes = 25,
                    isScrapped = false,
                    isLiked = false
                )
            )
        }
    }

    fun fetchRisingCourses() {
        viewModelScope.launch {
            delay(300)
            _risingCourses.value = listOf(
                WalkCourseResponse(
                    id = 3,
                    title = "반려견과 한강 산책",
                    imageUrl = "https://example.com/image3.jpg",
                    tags = listOf("#자연친화", "#탐색활동"),
                    distanceMeters = 2000,
                    durationMinutes = 35,
                    isScrapped = false,
                    isLiked = false
                ),
                WalkCourseResponse(
                    id = 4,
                    title = "서울숲 동물친화코스",
                    imageUrl = "https://example.com/image4.jpg",
                    tags = listOf("#풍경좋음", "#초보자추천"),
                    distanceMeters = 3400,
                    durationMinutes = 45,
                    isScrapped = false,
                    isLiked = false
                )
            )
        }
    }

    fun postLike(courseId: Int) {
        viewModelScope.launch {
            try {
                val userId = 1 // 임시 테스트용 ID, 실제로는 TokenManager 등에서 가져오는 게 좋음
                val response = repository.postLike(LikeRequest(userId, courseId))
                if (response.isSuccessful) {
                    val body = response.body()
                    println("좋아요 성공: ${body?.message}")
                } else {
                    println("실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

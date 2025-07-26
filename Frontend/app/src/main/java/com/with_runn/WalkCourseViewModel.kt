package com.with_runn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.with_runn.WalkCourse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalkCourseViewModel : ViewModel() {

    private val _neighborhoodCourses = MutableLiveData<List<WalkCourse>>()
    val neighborhoodCourses: LiveData<List<WalkCourse>> = _neighborhoodCourses

    private val _risingCourses = MutableLiveData<List<WalkCourse>>()
    val risingCourses: LiveData<List<WalkCourse>> = _risingCourses

    // 서버 대신 더미 데이터로 채우기
    fun fetchNeighborhoodCourses() {
        viewModelScope.launch {
            delay(300) // 로딩 효과용
            _neighborhoodCourses.value = listOf(
                WalkCourse(
                    title = "망원한강공원",
                    tags = listOf("#초보자추천"),
                    imageResId = com.with_runn.R.drawable.image,
                    distance = "2.0km",
                    time = "30분"
                ),
                WalkCourse(
                    title = "연남동 코스",
                    tags = listOf("#풍경좋음"),
                    imageResId = com.with_runn.R.drawable.image,
                    distance = "1.8km",
                    time = "25분"
                )
            )
        }
    }

    fun fetchRisingCourses() {
        viewModelScope.launch {
            delay(300) // 로딩 효과용
            _risingCourses.value = listOf(
                WalkCourse(
                    title = "반려견과 한강 산책",
                    tags = listOf("#자연친화", "#탐색활동"),
                    imageResId = com.with_runn.R.drawable.image,
                    distance = "2.0km",
                    time = "35분"
                ),
                WalkCourse(
                    title = "서울숲 동물친화코스",
                    tags = listOf("#풍경좋음", "#초보자추천"),
                    imageResId = com.with_runn.R.drawable.image,
                    distance = "3.4km",
                    time = "45분"
                )
            )
        }
    }
}

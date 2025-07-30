package com.with_runn.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.with_runn.R
import com.with_runn.data.WalkCourse

class HotMoreViewModel : ViewModel() {

    private val _hotCourses = MutableLiveData<List<WalkCourse>>()
    val hotCourses: LiveData<List<WalkCourse>> = _hotCourses

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        _hotCourses.value = listOf(
            WalkCourse(
                title = "반려견과 한강 산책",
                tags = listOf("#자연친화", "#탐색활동"),
                imageResId = R.drawable.image,
                distance = "2.0km",
                time = "35분"
            ),
            WalkCourse(
                title = "연트럴파크 데이트길",
                tags = listOf("#후각자극", "#도심산책"),
                imageResId = R.drawable.image,
                distance = "1.5km",
                time = "20분"
            ),
            WalkCourse(
                title = "서울숲 동물친화코스",
                tags = listOf("#풍경좋음", "#초보자추천"),
                imageResId = R.drawable.image,
                distance = "3.4km",
                time = "45분"
            )
        )
    }
}
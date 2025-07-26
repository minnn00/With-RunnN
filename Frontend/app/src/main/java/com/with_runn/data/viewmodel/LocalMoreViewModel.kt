package com.with_runn.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.with_runn.R
import com.with_runn.data.WalkCourse


class LocalMoreViewModel : ViewModel() {

    private val _localCourses = MutableLiveData<List<WalkCourse>>()
    val localCourses: LiveData<List<WalkCourse>> = _localCourses

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        _localCourses.value = listOf(
            WalkCourse(
                title = "우리 동네 강아지 모임",
                tags = listOf("#이웃사촌", "#같이산책해요"),
                imageResId = R.drawable.img_dog_meeting,
                distance = "1.5km",
                time = "15분"
            ),
            WalkCourse(
                title = "발바닥 힐링길",
                tags = listOf("#부드러운산책", "#노령견"),
                imageResId = R.drawable.img_healing_trail,
                distance = "2.5km",
                time = "25분"
            ),
            WalkCourse(
                title = "놀이터 한 바퀴",
                tags = listOf("#애견운동장", "#사회성키우기"),
                imageResId = R.drawable.img_playground,
                distance = "1.3km",
                time = "12분"
            )
        )
    }
}
package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.Marker
import com.with_runn.data.course.CourseDetailResponse
import com.with_runn.data.course.CourseFetchRepository
import com.with_runn.data.course.CourseActionRepository
import com.with_runn.ui.course_edit.PinItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseDetailsViewModel(
    private val accessToken: String,
    private val fetchRepository: CourseFetchRepository,
    private val actionRepository: CourseActionRepository
) : ViewModel() {

    private val _courseData = MutableStateFlow<CourseDetailResponse?>(null)
    val courseData: StateFlow<CourseDetailResponse?> = _courseData.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private val _tempMarker = MutableStateFlow<Marker?>(null)
    val tempMarker: StateFlow<Marker?> = _tempMarker

    fun fetchCourse(courseId: Int) {
        viewModelScope.launch {
            try {
                val course = fetchRepository.getCourseDetail(courseId, accessToken)
                _courseData.value = course
                _isLiked.value = course.isLiked
                _isBookmarked.value = course.isScrapped
            } catch (e: Exception) {
                Log.e("CourseDetailsVM", "Course fetch failed", e)
            }
        }
    }

    fun toggleLike(): suspend () -> Boolean = {
        if(_courseData.value == null) false

        try {
            val courseId = _courseData.value!!.id
            val isSuccessful = if (_isLiked.value) {
                actionRepository.unlikeCourse(courseId, accessToken)
            } else {
                actionRepository.likeCourse(courseId, accessToken)
            }

            if (isSuccessful) {
                _isLiked.value = !_isLiked.value
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("CourseDetailsVM", "Like toggle failed", e)
            false
        }
    }

    fun toggleScrap(): suspend () -> Boolean = {
        if(_courseData.value == null) false
        try {
            val courseId = _courseData.value!!.id
            val isSuccessful = if (_isBookmarked.value) {
                actionRepository.unbookmarkCourse(courseId, accessToken)
            } else {
                actionRepository.bookmarkCourse(courseId, accessToken)
            }

            if (isSuccessful) {
                _isBookmarked.value = !_isBookmarked.value
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("CourseDetailsVM", "Bookmark toggle failed", e)
            false
        }
    }

    fun setTempMarker(marker: Marker?) {
        _tempMarker.value?.remove()
        _tempMarker.value = marker
    }

    fun removeTempMarker() {
        _tempMarker.value?.remove()
    }
}

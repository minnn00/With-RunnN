package com.with_runn.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.data.CourseDetailResponse
import com.with_runn.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseDetailViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courseDetail = MutableLiveData<CourseDetailResponse>()
    val courseDetail: LiveData<CourseDetailResponse> = _courseDetail

    fun fetchCourseDetail(courseId: Int) {
        viewModelScope.launch {
            val result = repository.getCourseDetail(courseId)
            result?.let {
                _courseDetail.postValue(it)
            }
        }
    }
}

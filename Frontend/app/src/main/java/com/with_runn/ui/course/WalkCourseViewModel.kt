package com.with_runn.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.data.course.CourseFetchRepository
import com.with_runn.data.course.CourseService
import com.with_runn.data.course.CourseSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WalkCourseViewModel : ViewModel() {

    private val repository = CourseFetchRepository(CourseService.api)

    // --- StateFlows ---
    private val _risingCourses = MutableStateFlow<List<CourseSummary>>(emptyList())
    val risingCourses: StateFlow<List<CourseSummary>> = _risingCourses

    private val _searchRisingCourses = MutableStateFlow<List<CourseSummary>>(emptyList())
    val searchRisingCourses: StateFlow<List<CourseSummary>> = _searchRisingCourses

    private val _nearbyCourses = MutableStateFlow<List<CourseSummary>>(emptyList())
    val nearbyCourses: StateFlow<List<CourseSummary>> = _nearbyCourses

    private val _searchNearbyCourses = MutableStateFlow<List<CourseSummary>>(emptyList())
    val searchNearbyCourses: StateFlow<List<CourseSummary>> = _searchNearbyCourses

    // --- Fetch functions ---
    fun fetchRisingCourses() {
        viewModelScope.launch {
            _risingCourses.value = repository.getRisingCourses()
        }
    }

    fun fetchSearchRisingCourses(keyword: String?) {
        viewModelScope.launch {
            _searchRisingCourses.value = repository.searchRisingCourses(keyword)
        }
    }

    fun fetchNearbyCourses(provinceId: Int, cityId: Int? = null, townId: Int? = null) {
        viewModelScope.launch {
            _nearbyCourses.value = repository.getNearbyCourses(provinceId, cityId, townId)
        }
    }

    fun fetchSearchNearbyCourses(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null,
        keyword: String? = null
    ) {
        viewModelScope.launch {
            _searchNearbyCourses.value =
                repository.searchNearbyCourses(provinceId, cityId, townId, keyword)
        }
    }
}

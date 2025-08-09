package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.R
import com.with_runn.data.RisingCourseResponse
import com.with_runn.data.WalkCourse
import com.with_runn.data.repository.CourseRepository
import kotlinx.coroutines.launch
import com.with_runn.data.TokenManager

class HotMoreViewModel : ViewModel() {

    private val repository = CourseRepository()
    private val _hotCourses = MutableLiveData<List<WalkCourse>>()
    private val _searchResults = MutableLiveData<List<RisingCourseResponse>>()
    val searchResults: LiveData<List<RisingCourseResponse>> = _searchResults
    val hotCourses: LiveData<List<WalkCourse>> get() = _hotCourses

    private fun getToken(): String = "Bearer ${TokenManager.getAccessToken() ?: ""}"

    fun fetchRisingCourses() {
        android.util.Log.d("HotMoreVM", "API 호출: fetchRisingCourses")
        viewModelScope.launch {
            try {
                val token = getToken()
                val response = repository.getRisingCourses(token)
                if (response.isSuccessful) {
                    val list = response.body()?.map {
                        WalkCourse(
                            id = it.courseId,
                            title = it.name,
                            tags = it.keyword,
                            imageResId = R.drawable.image,
                            distance = "",
                            time = it.time,
                            imageUrl = it.courseImage
                        )
                    } ?: emptyList()
                    android.util.Log.d("HotMoreVM", "API Success: ${list.size}개 / $list")
                    _hotCourses.value = list
                } else {
                    android.util.Log.e("HotMoreVM", "API Error: ${response.code()} / ${response.errorBody()?.string()}")
                    _hotCourses.value = emptyList()
                }
            } catch (e: Exception) {
                android.util.Log.e("HotMoreVM", "API Exception: ${e.message}")
                _hotCourses.value = emptyList()
            }
        }
    }

    fun searchRisingCourses(keyword: String) {
        viewModelScope.launch {
            try {
                val token = getToken()
                val response = repository.searchRisingCourses(token, keyword)
                if (response.isSuccessful) {
                    _searchResults.value = response.body() ?: emptyList()
                    Log.d("HotMoreVM", "떠오르는 검색 성공: ${response.body()}")
                } else {
                    Log.e("HotMoreVM", "떠오르는 검색 실패: ${response.code()} / ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("HotMoreVM", "떠오르는 검색 에러: ${e.message}")
            }
        }
    }
}


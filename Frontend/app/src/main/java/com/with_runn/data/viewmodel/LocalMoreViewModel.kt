package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.R
import com.with_runn.data.NeighborhoodPreviewResponse
import com.with_runn.data.WalkCourse
import com.with_runn.data.repository.CourseRepository
import kotlinx.coroutines.launch
import com.with_runn.data.TokenManager

class LocalMoreViewModel : ViewModel() {
    private val repository = CourseRepository()

    private val _localCourses = MutableLiveData<List<WalkCourse>>()
    val localCourses: LiveData<List<WalkCourse>> get() = _localCourses

    private val _searchResults = MutableLiveData<List<NeighborhoodPreviewResponse>>()
    val searchResults: LiveData<List<NeighborhoodPreviewResponse>> = _searchResults


    private fun getToken(): String = "Bearer ${TokenManager.getAccessToken() ?: ""}"

    fun fetchNearbyCourses(provinceId: Int, cityId: Int? = null, townId: Int? = null) {
        viewModelScope.launch {
            try {
                val token = getToken()
                val response = repository.getNearbyCourses(token, provinceId, cityId, townId)
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
                    android.util.Log.d("LocalMoreVM", "API Success: ${list.size}개 / $list")
                    _localCourses.value = list
                } else {
                    android.util.Log.e("LocalMoreVM", "API Error: ${response.code()} / ${response.errorBody()?.string()}")
                    _localCourses.value = emptyList()
                }
            } catch (e: Exception) {
                android.util.Log.e("LocalMoreVM", "API Exception: ${e.message}")
                _localCourses.value = emptyList()
            }
        }
    }

    fun searchCourses(provinceId: Int, keyword: String, cityId: Int? = null, townId: Int? = null) {
        viewModelScope.launch {
            try {
                val token = getToken()
                val response = repository.searchNearbyCourses(token, provinceId, cityId, townId, keyword)
                if (response.isSuccessful) {
                    _searchResults.value = response.body() ?: emptyList()
                } else {
                    _searchResults.value = emptyList()
                    Log.e("CourseSearch", "검색 실패: ${response.code()} / ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                Log.e("CourseSearch", "예외: ${e.localizedMessage}")
            }
        }
    }
}


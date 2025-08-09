package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.with_runn.data.NeighborhoodPreviewResponse
import com.with_runn.data.RisingPreviewResponse
import com.with_runn.data.WalkCourse
import com.with_runn.data.repository.CourseRepository
import com.with_runn.data.TokenManager
import kotlinx.coroutines.launch

class WalkCourseViewModel : ViewModel() {

    private val repository = CourseRepository()

    private fun getToken(): String {
        val raw = TokenManager.getAccessToken()
        Log.d("WalkCourseVM", "rawToken(len=${raw?.length}): ${raw?.take(15)}...")
        return "Bearer ${raw ?: ""}"
    }

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
                val token = getToken()
                Log.d("WalkCourseVM", "[NEARBY] req → p=$provinceId c=$cityId t=$townId")
                Log.d(
                    "WalkCourseVM",
                    "[NEARBY] token(len=${TokenManager.getAccessToken()?.length}) head=${
                        TokenManager.getAccessToken()?.take(12)
                    }"
                )

                val response = repository.getNeighborhoodPreview(token, provinceId, cityId, townId)
                Log.d("WalkCourseVM", "[NEARBY] code=${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body().orEmpty()
                    Log.d("WalkCourseVM", "[NEARBY] ok, size=${body.size}")
                    _neighborhoodPreview.value = body
                } else {
                    Log.e(
                        "WalkCourseVM",
                        "[NEARBY] fail code=${response.code()} err=${
                            response.errorBody()?.string()
                        }"
                    )
                    _neighborhoodPreview.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("WalkCourseVM", "[NEARBY] exception=${e.message}", e)
                _neighborhoodPreview.value = emptyList()
            }
        }
    }

    fun fetchRisingPreview() {
        viewModelScope.launch {
            try {
                val result = repository.getRisingPreview(getToken())
                Log.d("WalkCourseVM", "떠오르는 코스 미리보기 API 응답: $result")
                _risingPreview.value = result ?: emptyList()
            } catch (e: Exception) {
                Log.e("WalkCourseVM", "떠오르는 코스 미리보기 API 예외: ${e.message}")
                e.printStackTrace()
                _risingPreview.value = emptyList()
            }
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
            )
            // ... 더 추가 가능!
        )
    }
}

    // (만약 risingCourses, neighborhoodCourses 등 리스트를 실제로 API로 가져오면 아래처럼 추가로 함수 만들어서 token 넣어주세요.)
    // fun fetchNeighborhoodCourses(...) { ... }
    // fun fetchRisingCourses(...) { ... }


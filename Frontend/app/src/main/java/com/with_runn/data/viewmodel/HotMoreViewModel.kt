package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.with_runn.R
import com.with_runn.data.WalkCourse
import com.with_runn.data.repository.CourseRepository
import com.with_runn.data.TokenManager
import kotlinx.coroutines.launch

class HotMoreViewModel : ViewModel() {

    companion object { private const val NO_KM = "– km" }

    private val repository = CourseRepository()
    private val _hotCourses = MutableLiveData<List<WalkCourse>>()
    val hotCourses: LiveData<List<WalkCourse>> get() = _hotCourses
    private val _searchResults = MutableLiveData<List<WalkCourse>>()
    val searchResults: LiveData<List<WalkCourse>> = _searchResults

    private fun getToken(): String = "Bearer ${TokenManager.getAccessToken() ?: ""}"

    // "HH:mm:ss" / "30" / "30분" -> "N분"
    private fun minuteStringFrom(raw: String?): String? {
        val t = raw ?: return null
        val parts = t.split(":")
        val min = if (parts.size == 3) {
            (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
        } else t.filter { it.isDigit() }.toIntOrNull() ?: 0
        return if (min > 0) "${min}분" else null
    }

    // 1200(m) -> "1.2km"
    private fun kmFromMeters(m: Int): String =
        if (m % 1000 == 0) "${m / 1000}km" else String.format("%.1fkm", m / 1000.0)

    // ★ 핵심: DTO 안에 distanceMeters(Int?) 또는 distance(String?)가 있으면 사용, 없으면 "– km"
    private fun distanceTextFrom(dto: Any): String {
        // 1) distanceMeters (Int/Long/Double/Number…)
        val meters = runCatching {
            val f = dto::class.java.getDeclaredField("distanceMeters")
            f.isAccessible = true
            (f.get(dto) as? Number)?.toInt()
        }.getOrNull()
        if (meters != null && meters > 0) return kmFromMeters(meters)

        // 2) distance (String, 이미 "1.3km" 같은 경우)
        val str = runCatching {
            val f = dto::class.java.getDeclaredField("distance")
            f.isAccessible = true
            f.get(dto) as? String
        }.getOrNull()
        if (!str.isNullOrBlank()) return str

        // 3) 둘 다 없거나 0이면 대시
        return NO_KM
    }

    fun fetchRisingCourses() {
        viewModelScope.launch {
            try {
                val res = repository.getRisingCourses(getToken())
                if (res.isSuccessful) {
                    val list = res.body()?.map { dto ->
                        WalkCourse(
                            id = dto.courseId,
                            title = dto.name,
                            tags = dto.keyword ?: emptyList(),
                            imageResId = R.drawable.image,
                            imageUrl = dto.courseImage,
                            distance = distanceTextFrom(dto),       // ← 여기만 호출
                            time = dto.time?.toString()?.let { t ->
                                val parts = t.split(":")
                                val minutes = if (parts.size == 3) {
                                    (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
                                } else {
                                    t.filter(Char::isDigit).toIntOrNull() ?: 0
                                }
                                if (minutes > 0) "${minutes}분" else null
                            },
                            isScrapped = false,
                            isLiked = false
                        )
                    } ?: emptyList()
                    _hotCourses.value = list
                } else {
                    Log.e("HotMoreVM","code=${res.code()} err=${res.errorBody()?.string()}")
                    _hotCourses.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("HotMoreVM","ex=${e.message}", e); _hotCourses.value = emptyList()
            }
        }
    }

    fun searchRisingCourses(keyword: String) {
        viewModelScope.launch {
            try {
                val res = repository.searchRisingCourses(getToken(), keyword)
                if (res.isSuccessful) {
                    val list = res.body()?.map { dto ->
                        WalkCourse(
                            id = dto.courseId,
                            title = dto.name,
                            tags = dto.keyword ?: emptyList(),
                            imageResId = R.drawable.image,
                            imageUrl = dto.courseImage,
                            distance = distanceTextFrom(dto),       // ← 동일
                            time = minuteStringFrom(dto.time),
                            isScrapped = false,
                            isLiked = false
                        )
                    } ?: emptyList()
                    _searchResults.value = list
                } else {
                    Log.e("HotMoreVM","search fail ${res.code()}")
                }
            } catch (e: Exception) {
                Log.e("HotMoreVM","search ex=${e.message}", e)
            }
        }
    }
}



package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.with_runn.R
import com.with_runn.data.NeighborhoodPreviewResponse
import com.with_runn.data.WalkCourse
import com.with_runn.data.TokenManager
import com.with_runn.data.repository.CourseRepository
import kotlinx.coroutines.launch

private const val TAG = "LocalMoreVM"

class LocalMoreViewModel : ViewModel() {

    private val repository = CourseRepository()

    private val _localCourses = MutableLiveData<List<WalkCourse>>()
    val localCourses: LiveData<List<WalkCourse>> get() = _localCourses

    // 원본 DTO 그대로(기존 호환 용)
    private val _searchResults = MutableLiveData<List<NeighborhoodPreviewResponse>>()
    val searchResults: LiveData<List<NeighborhoodPreviewResponse>> = _searchResults

    // UI용으로 바로 쓸 수 있게 매핑된 검색 결과
    private val _searchCoursesUi = MutableLiveData<List<WalkCourse>>()
    val searchCoursesUi: LiveData<List<WalkCourse>> = _searchCoursesUi

    private fun getToken(): String = "Bearer ${TokenManager.getAccessToken() ?: ""}"

    // ---------- 헬퍼 ----------

    private fun titleFrom(name: String?): String =
        name?.trim()
            ?.takeUnless { it.equals("string", true) || it.isEmpty() }
            ?: "(제목 없음)"

    private fun parseTagString(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(',', '#', ' ')
            .map { it.trim().removePrefix("#") }
            .filter { it.isNotBlank() }
    }

    /** 태그: keyword 계열만 사용. 비어있으면 태그 표시 안 함(위치 fallback 제거). */
    private fun tagsFrom(dto: NeighborhoodPreviewResponse): List<String> {
        val listFromArrays = listOfNotNull(
            dto.keyword, dto.keywords, dto.keyWords
        ).firstOrNull { !it.isNullOrEmpty() }?.map { it.trim() } ?: emptyList()

        val listFromText = parseTagString(dto.keywordText)

        return (listFromArrays + listFromText)
            .map { it.removePrefix("#") }
            .filter { it.isNotBlank() }
            .distinct()
            .take(2)
            .map { "#$it" }
    }

    /** (구버전 호환) 위치 fallback 제거 */
    @Suppress("unused")
    private fun tagsFrom(keyword: List<String>?, location: String?): List<String> {
        val k = keyword?.map { it.trim() }?.filter { it.isNotBlank() }.orEmpty()
        return k.take(2).map { if (it.startsWith("#")) it else "#$it" }
    }

    /** 시간: 어떤 형태든 파싱해서 없으면도 "0분"을 반환 */
    private fun minuteStringOrZero(raw: Any?): String {
        return when (raw) {
            null -> "0분"
            is Int -> "${maxOf(0, raw)}분"
            is String -> {
                val s = raw.trim()
                if (s.isEmpty() || s.equals("string", true)) return "0분"
                val lower = s.lowercase()

                // ISO-8601: PT35M, PT1H20M
                if (lower.startsWith("pt")) {
                    val h = Regex("(\\d+)h").find(lower)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
                    val m = Regex("(\\d+)m").find(lower)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
                    val total = (h * 60 + m).coerceAtLeast(0)
                    return "${total}분"
                }

                // "HH:mm[:ss]" / "mm:ss"
                if (s.contains(":")) {
                    val p = s.split(":")
                    val h = p.getOrNull(0)?.toIntOrNull() ?: 0
                    val m = p.getOrNull(1)?.toIntOrNull() ?: 0
                    val total = if (p.size >= 3) h * 60 + m else if (p.size == 2) h * 60 + m else m
                    return "${maxOf(0, total)}분"
                }

                // "35분" / "35"
                val n = s.filter { it.isDigit() }.toIntOrNull() ?: 0
                "${maxOf(0, n)}분"
            }
            else -> "0분"
        }
    }

    /** 거리: 지금 DTO엔 필드가 없으므로 null 반환(화면에서 "– km").
     *  서버가 주기 시작하면 아래 주석 2줄만 풀면 자동 반영됨.
     */
    private fun distanceFrom(dto: NeighborhoodPreviewResponse): String? {
        // dto.distanceMeters?.let { m ->
        //     if (m > 0) return if (m % 1000 == 0) "${m / 1000}km" else String.format("%.1fkm", m / 1000.0)
        // }
        // dto.distance?.let { s -> if (s.isNotBlank()) return s }
        return null
    }

    private fun normalizeImageUrl(raw: String?): String? {
        val v = raw?.trim()
        if (v.isNullOrEmpty() || v.equals("string", true)) return null
        return if (v.startsWith("http", true)) v else "http://13.209.75.209:8080/$v"
    }

    private fun logTimeSamples(label: String, list: List<NeighborhoodPreviewResponse>) {
        list.take(3).forEachIndexed { idx, dto ->
            val parsed = minuteStringOrZero(dto.time)
            Log.d(TAG, "[$label] timeSample[$idx] raw=${dto.time} -> parsed=$parsed / name=${dto.name}")
        }
    }

    // ---------- API ----------

    fun fetchNearbyCourses(provinceId: Int, cityId: Int? = null, townId: Int? = null) {
        viewModelScope.launch {
            val token = getToken()
            Log.d(TAG, "▶ fetchNearbyCourses p=$provinceId c=$cityId t=$townId")
            try {
                val resp = repository.getNearbyCourses(token, provinceId, cityId, townId)
                Log.d(TAG, "◀ code=${resp.code()} ok=${resp.isSuccessful}")
                if (!resp.isSuccessful) {
                    Log.e(TAG, "error=${resp.errorBody()?.string()}")
                    _localCourses.value = emptyList()
                    return@launch
                }

                val body = resp.body().orEmpty()
                Log.d(TAG, "body.size=${body.size} sample=${body.take(1)}")
                logTimeSamples("nearby", body)

                val ui = body.map { dto ->
                    val t = minuteStringOrZero(dto.time)
                    WalkCourse(
                        id         = dto.courseId,
                        title      = titleFrom(dto.name),
                        tags       = tagsFrom(dto),                 // 위치 fallback 제거
                        imageResId = R.drawable.image,
                        imageUrl   = normalizeImageUrl(dto.courseImage),
                        distance   = distanceFrom(dto),             // null → Fragment에서 “– km”
                        time       = t,                             // 항상 표기 (없어도 "0분")
                        isScrapped = false,
                        isLiked    = false
                    )
                }
                Log.d(TAG, "mapped.size=${ui.size} first=${ui.firstOrNull()}")
                _localCourses.value = ui
            } catch (e: Exception) {
                Log.e(TAG, "EXCEPTION ${e.message}", e)
                _localCourses.value = emptyList()
            }
        }
    }

    fun searchCourses(provinceId: Int, keyword: String, cityId: Int? = null, townId: Int? = null) {
        viewModelScope.launch {
            val token = getToken()
            Log.d(TAG, "▶ searchCourses p=$provinceId keyword='$keyword' c=$cityId t=$townId")
            try {
                val resp = repository.searchNearbyCourses(token, provinceId, cityId, townId, keyword)
                Log.d(TAG, "◀ search code=${resp.code()} ok=${resp.isSuccessful}")
                if (!resp.isSuccessful) {
                    Log.e(TAG, "search.error=${resp.errorBody()?.string()}")
                    _searchResults.value = emptyList()
                    _searchCoursesUi.value = emptyList()
                    return@launch
                }

                val body = resp.body().orEmpty()
                Log.d(TAG, "search.body.size=${body.size} sample=${body.take(1)}")
                logTimeSamples("search", body)

                _searchResults.value = body // 기존 호환

                val ui = body.map { dto ->
                    val t = minuteStringOrZero(dto.time)
                    WalkCourse(
                        id         = dto.courseId,
                        title      = titleFrom(dto.name),
                        tags       = tagsFrom(dto),                 // 위치 fallback 제거
                        imageResId = R.drawable.image,
                        imageUrl   = normalizeImageUrl(dto.courseImage),
                        distance   = distanceFrom(dto),
                        time       = t,
                        isScrapped = false,
                        isLiked    = false
                    )
                }
                Log.d(TAG, "search.mapped.size=${ui.size} first=${ui.firstOrNull()}")
                _searchCoursesUi.value = ui
            } catch (e: Exception) {
                Log.e(TAG, "search.EXCEPTION ${e.message}", e)
                _searchResults.value = emptyList()
                _searchCoursesUi.value = emptyList()
            }
        }
    }
}

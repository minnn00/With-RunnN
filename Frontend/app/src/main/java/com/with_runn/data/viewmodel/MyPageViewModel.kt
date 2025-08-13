package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.with_runn.data.MyCourse
import com.with_runn.data.ProfileResult
import com.with_runn.data.TokenManager
import com.with_runn.data.WalkCourseResponse
import com.with_runn.data.repository.MyPageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyPageViewModel(private val repository: MyPageRepository) : ViewModel() {

    private val _scrapList = MutableLiveData<List<WalkCourseResponse>>()
    val scrapList: LiveData<List<WalkCourseResponse>> = _scrapList

    private val _likeList = MutableLiveData<List<WalkCourseResponse>>()
    val likeList: LiveData<List<WalkCourseResponse>> = _likeList

    private val _myCourseList = MutableLiveData<List<MyCourse>>()
    val myCourseList: LiveData<List<MyCourse>> get() = _myCourseList

    private val _profile = MutableStateFlow<ProfileResult?>(null)
    val profile: StateFlow<ProfileResult?> = _profile

    private val _navigateToProfileSetup = MutableLiveData<Boolean>()
    val navigateToProfileSetup: LiveData<Boolean> = _navigateToProfileSetup


    private val gson = Gson()

    private fun minuteStringFromAny(raw: Any?): String? = when (raw) {
        null -> null
        is Int -> if (raw > 0) "${raw}분" else null
        is String -> {
            val parts = raw.split(":")
            val minutes = if (parts.size == 3) {
                (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
            } else raw.filter { it.isDigit() }.toIntOrNull() ?: 0
            if (minutes > 0) "${minutes}분" else null
        }
        else -> null
    }

    // Int? 미터 / "1500" 같은 문자열 → "x.xkm"
    private fun kmStringFromAny(raw: Any?): String? {
        val meters: Int = when (raw) {
            null -> return null
            is Int -> raw
            is String -> raw.filter { it.isDigit() }.toIntOrNull() ?: return null
            else -> return null
        }
        if (meters <= 0) return null
        return if (meters % 1000 == 0) "${meters / 1000}km"
        else String.format("%.1fkm", meters / 1000.0)
    }

    private fun normalizeImageUrl(raw: String?): String? {
        val v = raw?.trim()
        if (v.isNullOrEmpty() || v.equals("string", true)) return null
        return if (v.startsWith("http", true)) v else "http://13.209.75.209:8080/$v"
    }

    private fun normalizeTitle(name: String?, location: String?): String {
        val n = name?.trim()
        return if (!n.isNullOrEmpty() && !n.equals("string", true)) n
        else location ?: "(제목 없음)"
    }

    private fun parseMeters(raw: String?): Int {
        if (raw.isNullOrBlank()) return 0
        val s = raw.trim().lowercase()
        return when {
            s.endsWith("km") -> ((s.removeSuffix("km").trim().replace(",", ""))
                .toDoubleOrNull() ?: 0.0).times(1000).toInt()
            s.endsWith("m")  -> (s.removeSuffix("m").trim().replace(",", "")).toIntOrNull() ?: 0
            else             -> s.replace(",", "").toIntOrNull() ?: 0
        }.coerceAtLeast(0)
    }

    // "HH:mm:ss" / "30분" / "30" → minutes(Int)
    private fun parseMinutes(raw: String?): Int {
        if (raw.isNullOrBlank()) return 0
        val t = raw.trim()
        val parts = t.split(":")
        return if (parts.size == 3) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].toIntOrNull() ?: 0
            (h * 60 + m).coerceAtLeast(0)
        } else {
            // "30분", "30" 등
            (t.filter { it.isDigit() }.toIntOrNull() ?: 0).coerceAtLeast(0)
        }
    }

    private fun firstNumberOrNull(o: JsonObject, vararg keys: String): Double? {
        for (k in keys) {
            val e = o.get(k) ?: continue
            if (!e.isJsonNull) return runCatching { e.asDouble }.getOrNull()
        }
        return null
    }

    // 스크랩: id 목록 → 상세조회 → 화면모델
    fun loadScrapCourses() {
        viewModelScope.launch {
            val token = TokenManager.getAccessToken()
            if (token.isNullOrBlank()) { _scrapList.value = emptyList(); return@launch }

            try {
                val res = repository.getScrapCourses("Bearer $token")
                if (!res.isSuccessful) {
                    Log.e("Scrap", "code=${res.code()} err=${res.errorBody()?.string()}")
                    _scrapList.value = emptyList()
                    return@launch
                }

                val items = res.body()?.result?.scrapList ?: emptyList()
                val ids: List<Int> = items.mapNotNull { any ->
                    runCatching {
                        val obj = gson.toJsonTree(any).asJsonObject
                        firstLong(obj, "courseId").toInt()
                    }.getOrNull()
                }.distinct()

                val result = mutableListOf<WalkCourseResponse>()
                for (id in ids) {
                    val d = repository.getCourseDetail(id)
                    if (d.isSuccessful) {
                        d.body()?.let { body ->
                            detailToWalkCourseFlexible(body, liked = false)?.let(result::add)
                        }
                    } else {
                        Log.w("ScrapDetail", "id=$id code=${d.code()}")
                    }
                }
                _scrapList.value = result
            } catch (e: Exception) {
                Log.e("Scrap", "예외: ${e.message}", e)
                _scrapList.value = emptyList()
            }
        }
    }


    // 좋아요: id 목록 → 상세조회 → 화면모델

    fun loadLikedCourses() {
        viewModelScope.launch {
            val token = TokenManager.getAccessToken()
            if (token.isNullOrBlank()) { _likeList.value = emptyList(); return@launch }

            try {
                val res = repository.getLikedCourses("Bearer $token")
                if (!res.isSuccessful) {
                    Log.e("Like", "code=${res.code()} err=${res.errorBody()?.string()}")
                    _likeList.value = emptyList()
                    return@launch
                }

                val items = res.body()?.result?.likeList ?: emptyList()
                val ids: List<Int> = items.mapNotNull { any ->
                    runCatching {
                        val obj = gson.toJsonTree(any).asJsonObject
                        firstLong(obj, "courseId").toInt()
                    }.getOrNull()
                }.distinct()

                val result = mutableListOf<WalkCourseResponse>()
                for (id in ids) {
                    val d = repository.getCourseDetail(id)
                    if (d.isSuccessful) {
                        d.body()?.let { body ->
                            detailToWalkCourseFlexible(body, liked = true)?.let(result::add)
                        }
                    } else {
                        Log.w("LikeDetail", "id=$id code=${d.code()}")
                    }
                }
                _likeList.value = result
            } catch (e: Exception) {
                Log.e("Like", "예외: ${e.message}", e)
                _likeList.value = emptyList()
            }
        }
    }


    // 나의 산책코스

    fun loadMyCourses() {
        viewModelScope.launch {
            try {
                Log.d("MyPageVM", "loadMyCourses() 호출")
                val response = repository.getMyCourses()
                val list = response?.result?.myCourseList ?: emptyList()
                Log.d("MyPageVM", "loadMyCourses() 성공 size=${list.size}")
                _myCourseList.value = list
            } catch (e: Exception) {
                Log.e("MyPageVM", "API 호출 실패: ${e.message}", e)
            }
        }
    }

    // 추가 헬퍼
    fun reloadMyCourses() {
        Log.d("MyPageVM", "reloadMyCourses() called")
        loadMyCourses()
    }

    fun loadMyCoursesIfEmpty() {
        if (_myCourseList.value.isNullOrEmpty()) {
            Log.d("MyPageVM", "loadMyCoursesIfEmpty() -> empty, loading")
            loadMyCourses()
        } else {
            Log.d("MyPageVM", "loadMyCoursesIfEmpty() -> already loaded (${_myCourseList.value?.size})")
        }
    }


    // 프로필 (기존 유지)

    fun loadUserProfile() = viewModelScope.launch {
        try {
            val res = repository.getUserProfile()
            if (res.isSuccessful) {
                _profile.value = res.body()?.result
            } else {
                Log.e("MyPageVM", "프로필 실패: ${res.code()} / ${res.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("MyPageVM", "프로필 예외", e)
        }
    }


    // 상세 응답 → 카드모델 (응답 스키마가 달라도 동작하게 유연 매핑)
    private fun detailToWalkCourseFlexible(
        detailBody: Any,
        liked: Boolean
    ): WalkCourseResponse? {
        val root = gson.toJsonTree(detailBody).asJsonObject
        val course = pickCourseNode(root) ?: return null

        val id    = firstLong(course, "id", "courseId").toInt()
        val title = firstString(course, "title", "courseTitle", "name").ifBlank { "제목 없음" }
        val imageUrl = firstString(course, "thumbnailUrl", "imageUrl", "thumbnail", "image")
        val tags  = firstArrayOfString(course, "keyWords", "keywords", "keyword", "tags", "tagList", "labels")

        // --- 거리 ---
        // 숫자형: 서버가 km로 보내기도, m로 보내기도 해서 휴리스틱 적용
        val num = firstNumberOrNull(
            course,
            "distanceMeters", "distance_m", "distance", "length",
            "distanceKm", "distance_km"
        )
        val str = firstString(course, "distance", "distanceStr", "courseDistance", "lengthText", "km")

        val distanceMeters = when {
            num != null -> when {
                // 0.1 ~ 50.0 같은 범위면 'km'로 보고 → m로 변환
                num in 0.1..50.0 -> (num * 1000.0).toInt()
                // 1000 이상이면 이미 'm'
                num >= 1000.0 -> num.toInt()
                // 그 외(정수 몇 m일 수도 있음)
                else -> num.toInt()
            }
            else -> parseMeters(str)
        }.coerceAtLeast(0).coerceAtMost(Int.MAX_VALUE)

        // --- 시간 ---
        val durNum = firstNumberOrNull(course, "durationMinutes", "duration_min", "duration")
        val timeStr = firstString(course, "time", "courseTime", "durationText")
        val durationMinutes = (durNum?.toInt()?.takeIf { it > 0 } ?: parseMinutes(timeStr))
            .coerceAtLeast(0)

        return WalkCourseResponse(
            id = id,
            title = title,
            imageUrl = imageUrl,
            tags = tags,
            distanceMeters = distanceMeters,
            durationMinutes = durationMinutes,
            isScrapped = !liked,
            isLiked = liked
        )
    }

    // root → result/data → course 순으로 찾아 코스 오브젝트 반환
    private fun pickCourseNode(root: JsonObject): JsonObject? {
        var cur: JsonObject = root
        listOf("result", "data").forEach { key ->
            cur.get(key)?.let { if (it.isJsonObject) cur = it.asJsonObject }
        }
        cur.get("course")?.let { if (it.isJsonObject) return it.asJsonObject }
        return cur
    }

    // ===== Json 헬퍼들 =====
    private fun firstString(o: JsonObject, vararg keys: String): String {
        for (k in keys) {
            val e: JsonElement? = o.get(k)
            if (e != null && !e.isJsonNull) return runCatching { e.asString }.getOrNull() ?: ""
        }
        return ""
    }

    private fun firstLong(o: JsonObject, vararg keys: String): Long {
        for (k in keys) {
            val e = o.get(k)
            if (e != null && !e.isJsonNull) return runCatching { e.asLong }.getOrNull() ?: 0L
        }
        return 0L
    }

    private fun firstNumber(o: JsonObject, vararg keys: String): Double {
        for (k in keys) {
            val e = o.get(k)
            if (e != null && !e.isJsonNull) return runCatching { e.asDouble }.getOrNull() ?: 0.0
        }
        return 0.0
    }

    private fun firstArrayOfString(o: JsonObject, vararg keys: String): List<String> {
        for (k in keys) {
            val e = o.get(k) ?: continue
            if (e.isJsonArray) {
                return e.asJsonArray.mapNotNull { el ->
                    runCatching { el.asString.trim() }.getOrNull()
                }.filter { it.isNotBlank() }
            }
            if (e.isJsonPrimitive) {
                val raw = runCatching { e.asString }.getOrNull() ?: continue
                // 쉼표/공백/해시태그 구분자 모두 대응
                return raw.split(',', '#')
                    .flatMap { it.split(' ') }
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
            }
        }
        return emptyList()
    }
}

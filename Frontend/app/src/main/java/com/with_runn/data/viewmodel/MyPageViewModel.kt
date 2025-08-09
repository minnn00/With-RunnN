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
import kotlinx.coroutines.launch

class MyPageViewModel(private val repository: MyPageRepository) : ViewModel() {

    private val _scrapList = MutableLiveData<List<WalkCourseResponse>>()
    val scrapList: LiveData<List<WalkCourseResponse>> = _scrapList

    private val _likeList = MutableLiveData<List<WalkCourseResponse>>()
    val likeList: LiveData<List<WalkCourseResponse>> = _likeList

    private val _myCourseList = MutableLiveData<List<MyCourse>>()
    val myCourseList: LiveData<List<MyCourse>> get() = _myCourseList

    private val _profile = MutableLiveData<ProfileResult?>()
    val profile: LiveData<ProfileResult?> = _profile

    private val _navigateToProfileSetup = MutableLiveData<Boolean>()
    val navigateToProfileSetup: LiveData<Boolean> = _navigateToProfileSetup

    private val gson = Gson()

    // -----------------------------
    // 스크랩: id 목록 → 상세조회 → 화면모델
    // -----------------------------
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
                // courseId만 꺼내기 (타입이 정해져 있지 않아도 동작)
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

    // -----------------------------
    // 좋아요: id 목록 → 상세조회 → 화면모델
    // -----------------------------
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

    // -----------------------------
    // 나의 산책코스 (기존 유지)
    // -----------------------------
    fun loadMyCourses() {
        viewModelScope.launch {
            try {
                val response = repository.getMyCourses()
                _myCourseList.value = response?.result?.myCourseList ?: emptyList()
            } catch (e: Exception) {
                Log.e("MyPageVM", "API 호출 실패: ${e.message}")
            }
        }
    }

    // -----------------------------
    // 프로필 (기존 유지)
    // -----------------------------
    fun loadUserProfile() = viewModelScope.launch {
        try {
            val res = repository.getUserProfile()
            if (res.isSuccessful) {
                _profile.postValue(res.body()?.result)
            } else {
                Log.e("MyPageVM", "프로필 실패: ${res.code()} / ${res.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("MyPageVM", "프로필 예외", e)
        }
    }

    // =========================================================
    // 상세 응답 → 카드모델 (응답 스키마가 달라도 동작하게 유연 매핑)
    // =========================================================
    private fun detailToWalkCourseFlexible(
        detailBody: Any,
        liked: Boolean
    ): WalkCourseResponse? {
        // detail 응답에서 실제 코스 노드까지 내려가기: result / data / course 중 존재하는 것 선택
        val root = gson.toJsonTree(detailBody).asJsonObject
        val course = pickCourseNode(root) ?: return null

        val id = firstLong(course, "id", "courseId").toInt()
        val title = firstString(course, "title", "courseTitle", "name").ifBlank { "제목 없음" }
        val imageUrl = firstString(course, "thumbnailUrl", "imageUrl", "thumbnail", "image")
        val tags = firstArrayOfString(course, "tags", "tagList", "labels")

        // 거리/시간(m, min)로 통일
        val distanceMeters = firstNumber(course, "distanceMeters", "distance_m", "distance")
            .toLong().coerceAtLeast(0L)
            .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val durationMinutes = firstNumber(course, "durationMinutes", "duration_min", "duration")
            .toInt().coerceAtLeast(0)

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
        return cur // 최종적으로 필드가 모두 현재 노드에 있다면 그대로 사용
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
            val e = o.get(k)
            if (e != null && e.isJsonArray) {
                val arr: JsonArray = e.asJsonArray
                return arr.mapNotNull { el -> runCatching { el.asString }.getOrNull() }
            }
        }
        return emptyList()
    }
}

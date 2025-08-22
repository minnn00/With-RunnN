package com.with_runn.mypage

import com.with_runn.data.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File

class MyPageRepository(
    private val api: MyPageApi
) {
    // In-memory 캐시(StateFlow)
    private val _profile = MutableStateFlow<ProfileData?>(null)
    val profile: StateFlow<ProfileData?> = _profile

    private val _scraps = MutableStateFlow<List<CourseBrief>>(emptyList())
    val scraps: StateFlow<List<CourseBrief>> = _scraps

    private val _likes = MutableStateFlow<List<CourseBrief>>(emptyList())
    val likes: StateFlow<List<CourseBrief>> = _likes

    private val _followings = MutableStateFlow<List<FollowUser>>(emptyList())
    val followings: StateFlow<List<FollowUser>> = _followings

    private val _followers = MutableStateFlow<List<FollowUser>>(emptyList())
    val followers: StateFlow<List<FollowUser>> = _followers

    private val _myCourses = MutableStateFlow<List<CourseBrief>>(emptyList())
    val myCourses: StateFlow<List<CourseBrief>> = _myCourses

    private fun bearer(): String = "Bearer ${TokenManager.getAccessToken().orEmpty()}"

    suspend fun getProfile(): ProfileData? = withContext(Dispatchers.IO) {
        // CHANGED: 캐시 단락 제거, 항상 서버 조회
        val resp = api.getProfile(bearer())
        if (!resp.success) return@withContext null

        val r = resp.result
        val chars  = parseServerList(r.character)
        val styles = parseServerList(r.style)

        val mapped = ProfileData(
            provinceId = r.provinceId,
            cityId = r.cityId,
            townId = r.townId,
            name = r.name,
            gender = r.gender,
            birth = r.birth,
            breed = r.breed,
            size = r.size,
            characters = chars,
            style = styles,
            profileImage = r.profileImage,
            introduction = null
        )
        _profile.value = mapped
        mapped
    }
    // ---- Profile ----
    suspend fun patchProfile(body: ProfileUpdateRequest): Boolean = withContext(Dispatchers.IO) {
        val resp = api.patchProfile(bearer(), body)
        if (resp.success) {
            _profile.value = resp.result
            true
        } else false
    }

    suspend fun uploadProfileImage(file: File): Boolean = withContext(Dispatchers.IO) {
        // CHANGED: 업로드 성공 시 프로필 즉시 재동기화
        val reqBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val part = MultipartBody.Part.createFormData("file", file.name, reqBody)
        val resp = api.uploadProfileImage(bearer(), part)
        if (resp.success) {
            // 서버가 곧바로 새 URL을 반환하지 않는 경우를 대비, 재조회로 일관 동기화
            // 실패해도 업로드 성공 여부는 그대로 반환
            runCatching { getProfile() }
        }
        resp.success
    }

    // ---- Lists (prefetch + cache) ----
    suspend fun fetchScraps(force: Boolean = false): List<CourseBrief> = withContext(Dispatchers.IO) {
        // CHANGED: 항상 네트워크
        val resp = api.getScraps(bearer())
        val list = resp.result.scrapList
        _scraps.value = list
        list
    }

    suspend fun fetchLikes(force: Boolean = false): List<CourseBrief> = withContext(Dispatchers.IO) {
        // CHANGED: 항상 네트워크
        val resp = api.getLikes(bearer())
        val list = resp.result.likeList
        _likes.value = list
        list
    }

    suspend fun fetchFollowings(force: Boolean = false): List<FollowUser> = withContext(Dispatchers.IO) {
        // CHANGED: 항상 네트워크
        val resp = api.getFollowings(bearer())
        val list = resp.result.followings
        _followings.value = list
        list
    }

    suspend fun fetchFollowers(force: Boolean = false): List<FollowUser> = withContext(Dispatchers.IO) {
        // CHANGED: 항상 네트워크
        val resp = api.getFollowers(bearer())
        val list = resp.result.followings // 스키마 동일
        _followers.value = list
        list
    }

    suspend fun fetchMyCourses(force: Boolean = false): List<CourseBrief> = withContext(Dispatchers.IO) {
        // CHANGED: 항상 네트워크
        val resp = api.getMyCourses(bearer())
        val list = resp.result.myCourseList
        _myCourses.value = list
        list
    }

    // 병렬 프리패치(선택)
    suspend fun prefetchAll(force: Boolean = false) = withContext(Dispatchers.IO) {
        coroutineScope {
            listOf(
                async { fetchScraps(force) },
                async { fetchLikes(force) },
                async { fetchFollowings(force) },
                async { fetchFollowers(force) },
                async { fetchMyCourses(force) }
            ).awaitAll()   // 개별 await() 말고 한 번에 기다리면 깔끔해
        }
    }

    fun clearCaches() {
        _scraps.value = emptyList()
        _likes.value = emptyList()
        _followings.value = emptyList()
        _followers.value = emptyList()
        _myCourses.value = emptyList()
        _profile.value = null
    }

    private fun parseServerList(raw: String?): List<String>? {
        if (raw.isNullOrBlank()) return null
        val t = raw.trim()
        return if (t.startsWith("[")) {
            // ["활동적","차분"] 같은 케이스
            runCatching {
                val arr = org.json.JSONArray(t)
                (0 until arr.length())
                    .mapNotNull { arr.optString(it, null)?.trim() }
                    .filter { it.isNotEmpty() }
            }.getOrNull()
        } else {
            // "활동적, 차분" 같은 콤마/구분자 케이스
            t.split(',', '·', '/', '|')
                .map { it.trim().trim('"', '\'') }
                .filter { it.isNotEmpty() }
                .ifEmpty { null }
        }
    }
}
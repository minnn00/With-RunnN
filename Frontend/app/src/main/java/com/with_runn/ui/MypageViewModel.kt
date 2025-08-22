package com.with_runn.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.data.TokenManager
import com.with_runn.data.course.CourseActionRepository
import com.with_runn.data.course.CourseService
import com.with_runn.mypage.CourseBrief
import com.with_runn.mypage.FollowUser
import com.with_runn.mypage.MyPageRepository
import com.with_runn.mypage.ProfileData
import com.with_runn.mypage.ProfileUpdateRequest
import com.with_runn.mypage.MyPageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class MypageViewModel(
    private val repo: MyPageRepository = MyPageRepository(MyPageService.api)
) : ViewModel() {

    private val courseActions = CourseActionRepository(CourseService.actionApi)

    val profile:    StateFlow<ProfileData?>      = repo.profile
    val scraps:     StateFlow<List<CourseBrief>> = repo.scraps
    val likes:      StateFlow<List<CourseBrief>> = repo.likes
    val followings: StateFlow<List<FollowUser>>  = repo.followings
    val followers:  StateFlow<List<FollowUser>>  = repo.followers
    val myCourses:  StateFlow<List<CourseBrief>> = repo.myCourses

    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ---------- 초기 프리패치 ----------
    fun prefetchAll(force: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching { repo.prefetchAll(force) }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    // ---------- 각각 새로고침 ----------
    fun getProfile(onDone: ((ProfileData?) -> Unit)? = null) = viewModelScope.launch {
        _isLoading.value = true
        val data = runCatching { repo.getProfile() }
            .onFailure { _error.value = it.message }
            .getOrNull()
        _isLoading.value = false
        onDone?.invoke(data)
    }

    fun refreshProfile(force: Boolean = false) = viewModelScope.launch {
        _isLoading.value = true
        runCatching { repo.getProfile() }   // 캐시 있으면 캐시, 없으면 네트워크
            .onFailure { _error.value = it.message }
        _isLoading.value = false
    }

    fun refreshScraps(force: Boolean = false) = viewModelScope.launch {
        runCatching { repo.fetchScraps(force) }
            .onFailure { _error.value = it.message }
    }

    fun refreshLikes(force: Boolean = false) = viewModelScope.launch {
        runCatching { repo.fetchLikes(force) }
            .onFailure { _error.value = it.message }
    }

    fun refreshFollowings(force: Boolean = false) = viewModelScope.launch {
        runCatching { repo.fetchFollowings(force) }
            .onFailure { _error.value = it.message }
    }

    fun refreshFollowers(force: Boolean = false) = viewModelScope.launch {
        runCatching { repo.fetchFollowers(force) }
            .onFailure { _error.value = it.message }
    }

    fun refreshMyCourses(force: Boolean = false) = viewModelScope.launch {
        runCatching { repo.fetchMyCourses(force) }
            .onFailure { _error.value = it.message }
    }

    // ---------- 수정/업로드 ----------
    fun patchProfile(body: ProfileUpdateRequest, onDone: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val ok = runCatching { repo.patchProfile(body) }
                .onFailure { _error.value = it.message }
                .isSuccess
            _isLoading.value = false
            onDone?.invoke(ok)
        }
    }

    fun uploadProfileImage(file: File, onDone: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val ok = runCatching { repo.uploadProfileImage(file) }
                .onFailure { _error.value = it.message }
                .isSuccess
            _isLoading.value = false
            onDone?.invoke(ok)
        }
    }

    // 스크랩 해제(= 북마크 해제) 액션
    fun unbookmark(courseId: Int, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = TokenManager.getAccessToken().orEmpty()
            val ok = runCatching { courseActions.unbookmarkCourse(courseId, token) }
                .onFailure { _error.value = it.message }
                .getOrDefault(false)
            if (ok) runCatching { repo.fetchScraps(force = true) }
            onDone(ok)
        }
    }

    // 좋아요 해제 액션
    fun unlike(courseId: Int, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = TokenManager.getAccessToken().orEmpty()
            val ok = runCatching { courseActions.unlikeCourse(courseId, token) }
                .onFailure { _error.value = it.message }
                .getOrDefault(false)
            if (ok) runCatching { repo.fetchLikes(force = true) }
            onDone(ok)
        }
    }

    // ---------- 기타 ----------
    fun clearError() { _error.value = null }
    fun clearCaches() { repo.clearCaches() }
}

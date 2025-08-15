package com.with_runn.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.data.model.FollowResponse
import com.with_runn.data.model.Follower
import com.with_runn.data.repository.MypageFollowerRepository
import com.with_runn.ui.friend.model.dto.FriendDetailResponse
import kotlinx.coroutines.launch

class MypageFollowerViewmodel : ViewModel() {
    private val repository = MypageFollowerRepository()

    private val _followers = MutableLiveData<List<Follower>>()
    val followers: LiveData<List<Follower>> get() = _followers

    private val _followings = MutableLiveData<List<Follower>>()
    val followings: LiveData<List<Follower>> get() = _followings

    fun loadFollowers() {
        viewModelScope.launch {
            val result = repository.fetchFollowers()
            result?.let { _followers.value = it }
        }
    }

    fun loadFollowings() {
        viewModelScope.launch {
            val result = repository.fetchFollowings()
            result?.let { _followings.value = it }
        }
    }

    private val _friendDetail = MutableLiveData<FriendDetailResponse>()
    val friendDetail: LiveData<FriendDetailResponse> = _friendDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _followResult = MutableLiveData<Boolean>() // true = 팔로우 성공
    val followResult: LiveData<Boolean> = _followResult

    fun loadFriendDetail(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getFriendDetail(userId)
                .onSuccess { friendDetail ->
                    _friendDetail.value = friendDetail
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "친구 상세 정보 조회에 실패했습니다."
                }
            _isLoading.value = false
        }
    }

    fun followUser(userId: Int) {
        viewModelScope.launch {
            _error.value = null
            repository.followUser(userId)
                .onSuccess {
                    _followResult.value = true
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "팔로우 요청에 실패했습니다."
                }
        }
    }

    // MypageFollowerViewmodel
    suspend fun followUserAwait(userId: Int): FollowResponse {
        return try {
            when (val r = repository.followUser(userId)) {
                else -> r.getOrThrow()
            }
        } catch (e: Throwable) {
            FollowResponse(
                code = "ERR",
                message = e.message ?: "팔로우 요청에 실패했습니다.",
                result = "ERROR",
                success = false
            )
        }
    }
}

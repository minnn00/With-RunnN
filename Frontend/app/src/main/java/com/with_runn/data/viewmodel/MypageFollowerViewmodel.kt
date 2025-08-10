package com.with_runn.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            val result = repository.fetchFollowings() // 따로 구현 필요
            result?.let { _followings.value = it }
        }
    }

    private val _friendDetail = MutableLiveData<FriendDetailResponse>()
    val friendDetail: LiveData<FriendDetailResponse> = _friendDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

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
}
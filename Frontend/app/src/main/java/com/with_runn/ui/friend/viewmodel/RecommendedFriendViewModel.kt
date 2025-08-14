package com.with_runn.ui.friend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.ui.friend.model.dto.RecommendedFriendResponse
import com.with_runn.ui.friend.model.dto.FriendDetailResponse
import com.with_runn.ui.friend.repository.RecommendedFriendRepository
import kotlinx.coroutines.launch

class RecommendedFriendViewModel : ViewModel() {
    private val repository = RecommendedFriendRepository()
    
    private val _recommendedFriends = MutableLiveData<List<RecommendedFriendResponse>>()
    val recommendedFriends: LiveData<List<RecommendedFriendResponse>> = _recommendedFriends
    
    private val _allFriends = MutableLiveData<List<RecommendedFriendResponse>>()
    val allFriends: LiveData<List<RecommendedFriendResponse>> = _allFriends
    
    private val _friendDetail = MutableLiveData<FriendDetailResponse>()
    val friendDetail: LiveData<FriendDetailResponse> = _friendDetail

    private val _searchResults = MutableLiveData<List<RecommendedFriendResponse>>()
    val searchResults: LiveData<List<RecommendedFriendResponse>> = _searchResults

    private val _followResult = MutableLiveData<String>()
    val followResult: LiveData<String> = _followResult

    private val _blockResult = MutableLiveData<String>()
    val blockResult: LiveData<String> = _blockResult

    private val _reportResult = MutableLiveData<String>()
    val reportResult: LiveData<String> = _reportResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadRecommendedFriends(provinceId: Int, cityId: Int? = null, townId: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getRecommendedFriends(provinceId, cityId, townId)
                .onSuccess { friends ->
                    _recommendedFriends.value = friends
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "추천 친구 조회에 실패했습니다."
                    _isLoading.value = false
                }
        }
    }

    fun loadAllFriends(provinceId: Int, cityId: Int? = null, townId: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getAllFriends(provinceId, cityId, townId)
                .onSuccess { friends ->
                    _allFriends.value = friends
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "모든 친구 조회에 실패했습니다."
                    _isLoading.value = false
                }
        }
    }

    fun loadFriendDetail(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getFriendDetail(userId)
                .onSuccess { friendDetail ->
                    _friendDetail.value = friendDetail
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "친구 상세 정보 조회에 실패했습니다."
                    _isLoading.value = false
                }
        }
    }

    fun searchFriends(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null,
        keyword: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.searchFriends(provinceId, cityId, townId, keyword)
                .onSuccess { friends ->
                    _searchResults.value = friends
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "친구 검색에 실패했습니다."
                    _isLoading.value = false
                }
        }
    }

    fun followFriend(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.followFriend(userId)
                .onSuccess { result ->
                    _followResult.value = result
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "팔로우에 실패했습니다."
                    _isLoading.value = false
                }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun clearFollowResult() {
        _followResult.value = null
    }

    fun blockUser(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.blockUser(userId)
                .onSuccess { result ->
                    _blockResult.value = result
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "차단에 실패했습니다."
                    _isLoading.value = false
                }
        }
    }

    fun clearBlockResult() {
        _blockResult.value = null
    }
    
    fun clearError() {
        _error.value = null
    }

    fun reportFriend(reportedId: Int, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.reportFriend(reportedId, reason)
                .onSuccess { result ->
                    _reportResult.value = result
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "신고에 실패했습니다."
                    _isLoading.value = false
                }
        }
    }

    fun clearReportResult() {
        _reportResult.value = null
    }
} 
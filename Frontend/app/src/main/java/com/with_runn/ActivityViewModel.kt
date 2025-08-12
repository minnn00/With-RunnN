package com.with_runn

import androidx.lifecycle.ViewModel
import com.with_runn.data.TokenManager
import com.with_runn.data.region.RegionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ActivityViewModel: ViewModel() {
    private val _isBottomNavVisible = MutableStateFlow(true)
    val isBottomNavVisible : StateFlow<Boolean> = _isBottomNavVisible

    private val _isToolBarVisible = MutableStateFlow(true)
    val isToolBarVisible : StateFlow<Boolean> = _isToolBarVisible

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    private val _memberId = MutableStateFlow<Int?>(null)
    val memberId: StateFlow<Int?> = _memberId

    private val _firstRegion = MutableStateFlow<RegionResponse?>(null)
    val firstRegion: StateFlow<RegionResponse?> = _firstRegion

    private val _secondRegion = MutableStateFlow<RegionResponse?>(null)
    val secondRegion : StateFlow<RegionResponse?> = _secondRegion

    private val _thirdRegion = MutableStateFlow<RegionResponse?>(null)
    val thirdRegion : StateFlow<RegionResponse?> = _thirdRegion

    fun setBottomNavVisibility(isVisible: Boolean){
        _isBottomNavVisible.value = isVisible
    }

    fun setUpperToolbarVisibility(isVisible: Boolean){
        _isToolBarVisible.value = isVisible
    }

    fun loadToken() {
        _accessToken.value = TokenManager.getAccessToken()
        _memberId.value = TokenManager.getCurrentUserId()
    }

    /*fun clearToken() {
        _accessToken.value = null
        TokenManager.clearAccessToken()
    }*/

    fun updateSelectedRegion(firstRegion : RegionResponse, secondRegion : RegionResponse, thirdRegion : RegionResponse){
        _firstRegion.value = firstRegion
        _secondRegion.value = secondRegion
        _thirdRegion.value = thirdRegion
    }
}
package com.with_runn

import androidx.lifecycle.ViewModel
import com.with_runn.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ActivityViewModel: ViewModel() {
    private val _isBottomNavVisible = MutableStateFlow(true)
    val isBottomNavVisible : StateFlow<Boolean> = _isBottomNavVisible

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    fun setBottomNavVisibility(isVisible: Boolean){
        _isBottomNavVisible.value = isVisible
    }

    fun loadToken() {
        _accessToken.value = TokenManager.getAccessToken()
    }

    /*fun clearToken() {
        _accessToken.value = null
        TokenManager.clearAccessToken()
    }*/
}
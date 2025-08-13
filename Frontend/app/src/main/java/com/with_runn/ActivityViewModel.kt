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

    private val _selectedProvinceId = MutableStateFlow<Int?>(null)
    val selectedProvinceId: StateFlow<Int?> = _selectedProvinceId

    private val _selectedCityId = MutableStateFlow(-1)
    val selectedCityId: StateFlow<Int> = _selectedCityId

    private val _selectedTownId = MutableStateFlow(-1)
    val selectedTownId: StateFlow<Int> = _selectedTownId

    fun setBottomNavVisibility(isVisible: Boolean){
        _isBottomNavVisible.value = isVisible
    }

    fun setSelectedProvinceId(id: Int) {
        _selectedProvinceId.value = id
    }
    fun setSelectedCityId(id: Int) {
        _selectedCityId.value = id
    }
    fun setSelectedTownId(id: Int) {
        _selectedTownId.value = id
    }

    fun loadToken() {
        _accessToken.value = TokenManager.getAccessToken()
    }

    fun setLocation(provinceId: Int, cityId: Int, townId: Int) {
        _selectedProvinceId.value = provinceId
        _selectedCityId.value = cityId
        _selectedTownId.value = townId
    }

    /*fun clearToken() {
        _accessToken.value = null
        TokenManager.clearAccessToken()
    }*/
}
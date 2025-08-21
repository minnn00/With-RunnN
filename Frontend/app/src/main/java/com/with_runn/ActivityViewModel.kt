package com.with_runn

import androidx.lifecycle.ViewModel
import com.with_runn.data.TokenManager
import com.with_runn.data.region.RegionResponse
import com.with_runn.login.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class ActivityViewModel: ViewModel() {

    private val authRepo = AuthRepository()

    private val _isBottomNavVisible = MutableStateFlow(true)
    val isBottomNavVisible : StateFlow<Boolean> = _isBottomNavVisible

    private val _isToolBarVisible = MutableStateFlow(true)
    val isToolBarVisible : StateFlow<Boolean> = _isToolBarVisible

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

//    private val _selectedProvinceId = MutableStateFlow<Int?>(null)
//    val selectedProvinceId: StateFlow<Int?> = _selectedProvinceId

//    private val _selectedCityId = MutableStateFlow(-1)
//    val selectedCityId: StateFlow<Int> = _selectedCityId

//   private val _selectedTownId = MutableStateFlow(-1)
//    val selectedTownId: StateFlow<Int> = _selectedTownId

    private val _memberId = MutableStateFlow<Int?>(null)
    val memberId: StateFlow<Int?> = _memberId

    private val _firstRegion = MutableStateFlow<RegionItem>(
        RegionItem(9, "서울")
    )
    val firstRegion: StateFlow<RegionItem> = _firstRegion

    private val _secondRegion = MutableStateFlow<RegionItem?>(null)
    val secondRegion : StateFlow<RegionItem?> = _secondRegion

    private val _thirdRegion = MutableStateFlow<RegionItem?>(null)
    val thirdRegion : StateFlow<RegionItem?> = _thirdRegion

    fun setBottomNavVisibility(isVisible: Boolean){
        _isBottomNavVisible.value = isVisible
    }

  /*
    fun setSelectedProvinceId(id: Int) {
        _selectedProvinceId.value = id
    }
    fun setSelectedCityId(id: Int) {
        _selectedCityId.value = id
    }
    fun setSelectedTownId(id: Int) {
        _selectedTownId.value = id
*/
    fun setUpperToolbarVisibility(isVisible: Boolean){
        _isToolBarVisible.value = isVisible
    }

    fun loadToken() {
        _accessToken.value = TokenManager.getAccessToken()
        _memberId.value = TokenManager.getCurrentUserId()
    }

//    fun setLocation(provinceId: Int, cityId: Int, townId: Int) {
//        _selectedProvinceId.value = provinceId
//        _selectedCityId.value = cityId
//        _selectedTownId.value = townId
//    }

    /*fun clearToken() {
        _accessToken.value = null
        TokenManager.clearAccessToken()
    }*/

    suspend fun logout() = withContext(Dispatchers.IO) {
        TokenManager.clearAll()
        _accessToken.value = null
        _memberId.value = null
    }

    suspend fun deleteAccount(): Boolean = withContext(Dispatchers.IO) {
        val ok = authRepo.deleteAccount()
        if (ok) {
            TokenManager.clearAll()
            _accessToken.value = null
            _memberId.value = null
        }
        ok
    }

    fun updateSelectedRegion(firstRegion: RegionItem, secondRegion: RegionItem, thirdRegion: RegionItem){
        _firstRegion.value = firstRegion
        _secondRegion.value = secondRegion
        _thirdRegion.value = thirdRegion
    }
}
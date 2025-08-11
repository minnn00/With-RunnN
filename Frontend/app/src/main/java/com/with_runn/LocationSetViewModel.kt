package com.with_runn

import androidx.lifecycle.ViewModel
import com.with_runn.data.region.RegionRepository
import com.with_runn.data.region.RegionResponse
import com.with_runn.data.region.RegionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationSetViewModel : ViewModel() {

    private val repo = RegionRepository(RegionService.api)

    private val _firstRegion = MutableStateFlow<RegionResponse?>(null)
    val firstRegion : StateFlow<RegionResponse?> = _firstRegion

    private val _secondRegion = MutableStateFlow<RegionResponse?>(null)
    val secondRegion : StateFlow<RegionResponse?> = _secondRegion

    private val _thirdRegion = MutableStateFlow<RegionResponse?>(null)
    val thirdRegion : StateFlow<RegionResponse?> = _thirdRegion

    fun setFirstRegion(item: RegionResponse?){
        _firstRegion.value = item
    }

    fun setSecondRegion(item: RegionResponse?){
        _secondRegion.value = item
    }

    fun setThirdRegion(item: RegionResponse?){
        _thirdRegion.value = item
    }

    suspend fun fetchProvinces(token: String) = repo.getProvinces(token)
    suspend fun fetchCities(token: String, provinceId: Int) = repo.getCities(token, provinceId)
    suspend fun fetchTowns(token: String, cityId: Int) = repo.getTowns(token, cityId)
    suspend fun saveSelection(token: String, provinceId: Int, cityId: Int, townId: Int): Boolean {
        return repo.saveUserLocation(token, provinceId, cityId, townId).success
    }
}
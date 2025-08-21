package com.with_runn

import androidx.lifecycle.ViewModel
import com.with_runn.data.region.RegionRepository
import com.with_runn.data.region.RegionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationSetViewModel : ViewModel() {

    private val repo = RegionRepository(RegionService.api)

    private val _firstRegion = MutableStateFlow<RegionItem>(
        RegionItem(9, "서울")
    )
    val firstRegion : StateFlow<RegionItem> = _firstRegion

    private val _secondRegion = MutableStateFlow<RegionItem?>(null)
    val secondRegion : StateFlow<RegionItem?> = _secondRegion

    private val _thirdRegion = MutableStateFlow<RegionItem?>(null)
    val thirdRegion : StateFlow<RegionItem?> = _thirdRegion

    fun setFirstRegion(item: RegionItem){ _firstRegion.value = item }
    fun setSecondRegion(item: RegionItem?){ _secondRegion.value = item }
    fun setThirdRegion(item: RegionItem?){ _thirdRegion.value = item }

    suspend fun fetchProvinces(token: String) = repo.getProvinces(token)
    suspend fun fetchCities(token: String, provinceId: Int) = repo.getCities(token, provinceId)
    suspend fun fetchTowns(token: String, cityId: Int) = repo.getTowns(token, cityId)
    suspend fun saveSelection(token: String, provinceId: Int?, cityId: Int?, townId: Int?): Boolean {
        return repo.saveUserLocation(token, provinceId, cityId, townId).success
    }
}
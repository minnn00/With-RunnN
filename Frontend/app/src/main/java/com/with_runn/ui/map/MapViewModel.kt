package com.with_runn.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.with_runn.mapData.MapSearchItem
import com.with_runn.mapData.MapSearchRepository
import com.with_runn.mapData.MapSearchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


// TEMP
data class CustomMarker(
    val title: String,
    val snippet: String? = null,
    val position: LatLng
)

class MapViewModel : ViewModel(){

    private val repository = MapSearchRepository(MapSearchService.api)

    private var collectJob: kotlinx.coroutines.Job? = null

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted : StateFlow<Boolean> = _locationPermissionGranted

    private val _currentChipType = MutableStateFlow("")
    val currentChipType : StateFlow<String> = _currentChipType

    private val _facilityList = MutableStateFlow<List<MapSearchItem>>(emptyList())
    val facilityList: StateFlow<List<MapSearchItem>> = _facilityList

    private val _markers = MutableStateFlow<List<CustomMarker>>(emptyList())
    val markers : StateFlow<List<CustomMarker>> = _markers

    private val _tempMarker = MutableStateFlow<Marker?>(null)
    val tempMarker: StateFlow<Marker?> = _tempMarker

    fun addMarker(marker: CustomMarker){
        _markers.value = _markers.value + marker
    }

    fun setCurrentChipType(type: String){
        _currentChipType.value = type
    }

    fun setMarkers(markerList: List<CustomMarker>){
        _markers.value = markerList
    }

    fun removeMarker(targetLatLng: LatLng){
        _markers.value = _markers.value.filterNot{it.position == targetLatLng}
    }

    fun clearMarker(){
        _markers.value = emptyList()
    }

    fun setLocationPermission(permission : Boolean){
        _locationPermissionGranted.value = permission
    }

    fun setTempMarker(marker: Marker?){
        _tempMarker.value?.remove()
        _tempMarker.value = marker
    }

    fun removeTempMarker(){
        _tempMarker.value?.remove()
    }

    /**
     * ▷ 지역+카테고리 기반 시설 전체 로딩 (우리 BE 검색 API로 교체)
     * - sido / gugun / dong는 각각 province / city / town에 매핑
     * - category는 서버의 type 파라미터로 전달
     * - 서버 페이징을 last=true까지 루프하여 모두 적재
     * - 결과는 FacilityItem으로 매핑하여 _facilityList에 반영
     */
    // MapViewModel.kt — loadFacilities 교체(핵심만)
    fun loadFacilities(
        sido: String? = null,
        gugun: String? = null,
        dong: String? = null,
        category: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                val province = (sido ?: "서울")
                val city = gugun
                val town = dong

                // 1) 캐시 흐름 구독 → 캐시가 채워지면 자동으로 _facilityList 갱신
                collectJob?.cancel()
                collectJob = launch {
                    repository.flowFor(
                        type = category, province = province, city = city, town = town
                    ).collectLatest { cached ->
                        _facilityList.value = cached
                    }
                }

                // 2) 서버에서 전체 페이지 로드 → 캐시에 적재
                repository.loadAllAndCache(
                    type = category,
                    province = province,
                    city = city,
                    town = town,
                    pageSize = 50
                )
            }.onSuccess {
                onResult(true)
            }.onFailure { e ->
                Log.e("MapViewModel", "loadFacilities failed", e)
                if (_facilityList.value.isEmpty()) _facilityList.value = emptyList()
                onResult(false)
            }
        }
    }
    fun clearFacilityList(){
        _facilityList.value = emptyList()
    }
}
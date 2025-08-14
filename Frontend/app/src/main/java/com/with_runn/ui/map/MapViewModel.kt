package com.with_runn.ui.map

import FacilityItem
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.with_runn.BuildConfig
import com.with_runn.mapData.PetFacilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// TEMP
data class CustomMarker(
    val title: String,
    val snippet: String? = null,
    val position: LatLng
)

class MapViewModel : ViewModel(){

    private val repository = PetFacilityRepository()

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted : StateFlow<Boolean> = _locationPermissionGranted

    private val _currentChipType = MutableStateFlow("")
    val currentChipType : StateFlow<String> = _currentChipType

    private val _facilityList = MutableStateFlow<List<FacilityItem>>(emptyList())
    val facilityList: StateFlow<List<FacilityItem>> = _facilityList

    private val _markers = MutableStateFlow<List<CustomMarker>>(emptyList())
    val markers : StateFlow<List<CustomMarker>> = _markers

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

    fun loadFacilities(
        sido: String? = null,
        gugun: String? = null,
        dong: String? = null,
        category: String,
        onResult: (Boolean) -> Unit
    ){
        viewModelScope.launch {

            val result = repository.fetchFacilites(
                apiKey = BuildConfig.PET_API_KEY
            )

            result?.let{
                _facilityList.value = it
                onResult(true)
            } ?: run{
                Log.e("QUERY RESULT", "EMPTY RESULT")
                onResult(false)
            }
        }
    }

    fun clearFacilityList(){
        _facilityList.value = emptyList()
    }
}
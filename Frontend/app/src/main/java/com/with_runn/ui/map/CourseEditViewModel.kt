package com.with_runn.ui.map

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.with_runn.mapData.DirectionsRepository
import com.with_runn.ui.course_edit.CourseData
import com.with_runn.ui.course_edit.PinItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseEditViewModel : ViewModel() {
    private val repository = DirectionsRepository()

    private val _courseData = MutableStateFlow<CourseData>(
        CourseData(
            "샘플 데이터",
            "샘플 데이터",
            "샘플 키워드",
            90
        )
    )
    val courseData : StateFlow<CourseData> = _courseData

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted : StateFlow<Boolean> = _locationPermissionGranted

    private val _polyLineData = MutableStateFlow<List<LatLng>>(emptyList())
    val polyLineData : StateFlow<List<LatLng>> = _polyLineData

    private val _polyLine = MutableStateFlow<Polyline?>(null)
    val polyLine : StateFlow<Polyline?> = _polyLine

    private val _isPinning = MutableStateFlow(false)
    val isPinning : StateFlow<Boolean> = _isPinning

    private val _pinList = MutableStateFlow<List<PinItem>>(emptyList())
    val pinList: StateFlow<List<PinItem>> = _pinList.asStateFlow()

    private val _tempMarker = MutableStateFlow<Marker?>(null)
    val tempMarker: StateFlow<Marker?> = _tempMarker

    fun setPinList(newList: List<PinItem>) {
        val indexedList = newList.mapIndexed { idx, item ->
            item.copy(index = idx + 1)
        }
        _pinList.value = indexedList
    }

    fun setTempMarker(marker: Marker?){
        _tempMarker.value?.remove()
        _tempMarker.value = marker
    }

    fun removeTempMarker(){
        _tempMarker.value?.remove()
    }

    fun togglePinningBtn(){
        _isPinning.value = !isPinning.value
    }

    fun setLocationPermission(permission : Boolean){
        _locationPermissionGranted.value = permission
    }

    fun addPin(name: String, content: String = "", lat: Double, lng: Double){
        val current = _pinList.value
        val newPin = PinItem(
            current.size + 1,
            name = name,
            content = content,
            lat = lat,
            lng = lng,
        )

        _pinList.value = current + newPin
    }

    fun addPin(newPin: PinItem){
        val current = _pinList.value
        newPin.index = current.size + 1

        _pinList.value = current + newPin
    }

    fun updatePin(updated: PinItem) {
        val current = _pinList.value

        val updatedList = current.map { pin ->
            if (pin.lat == updated.lat && pin.lng == updated.lng) {
                updated.index = pin.index
                updated
            }else {
                pin
            }
        }

        _pinList.value = updatedList
    }

    fun setSampleData() {
        _pinList.value = listOf(
            PinItem(1, "경복궁", "경복궁", 37.579617, 126.977041),
            PinItem(2, "광화문광장", "광화문광장", 37.572441, 126.976814),
            PinItem(3, "서울시청", "서울시청", 37.566345, 126.977893),
            PinItem(4, "덕수궁", "덕수궁", 37.565804, 126.975145),
            PinItem(5, "서울역", "서울역", 37.553736, 126.969634)
        )
    }

    fun removePolylineData(){
        _polyLineData.value = emptyList()
    }

    fun setPolyline(polyline: Polyline){
        _polyLine.value = polyline
    }

    fun removePolyline(){
        _polyLine.value = null
    }

    fun askDirections(){
        viewModelScope.launch {
            val response = repository.getWalkingRouteFromPins(pinList.value)
            Log.e("DIRECTIONS", "status=${response?.status}, err=${response?.errorMessage}")

            val encoded = response?.routes?.firstOrNull()?.overviewPolyline?.points
            if (encoded != null) {
                _polyLineData.value = PolyUtil.decode(encoded)
            } else {
                _polyLineData.value = emptyList()
            }

        }
    }
}
package com.with_runn.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.PolyUtil
import com.with_runn.BuildConfig
import com.with_runn.data.course.CourseRepository
import com.with_runn.data.course.*
import com.with_runn.ui.course_edit.CourseData
import com.with_runn.ui.course_edit.PinItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.with_runn.tmap.TmapRetrofit
import com.with_runn.tmap.TmapDirectionsRepository
import okhttp3.MultipartBody

class CourseEditViewModel : ViewModel() {
    private val repository: TmapDirectionsRepository by lazy {
        val service = TmapRetrofit.createService()
        TmapDirectionsRepository(service, appKey = BuildConfig.TMAP_API_KEY)
    }

    private val courseRepo = CourseRepository()

    private val _mode = MutableStateFlow(CourseMode.CREATE)
    val mode: StateFlow<CourseMode> = _mode

    // 1회 하이드레이션 가드
    private val _detailHydrated = MutableStateFlow(false)
    val detailHydrated: StateFlow<Boolean> = _detailHydrated

    // 이미 있는 것들 외
    private val fetchRepo = CourseFetchRepository(CourseService.api)

    private val _loadedCourseId = MutableStateFlow<Int?>(null)
    val loadedCourseId: StateFlow<Int?> = _loadedCourseId

    private val _courseData = MutableStateFlow<CourseData>(
        CourseData(
            "",
            null,
            null,
            0
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
        _tempMarker.value = null
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
            val pins = pinList.value
            if (pins.size < 2) {
                _polyLineData.value = emptyList()
                return@launch
            }

            runCatching {
                repository.getWalkingRouteFromPins(pins)
            }.onSuccess { result ->
                _polyLineData.value = result.path

                // 시간 분 단위 변환 (올림)
                val minutes = result.totalTimeSeconds?.let { (it + 59) / 60 } ?: 0
                _courseData.value.time = minutes

            }.onFailure { t ->
                Log.e("TMAP_ROUTE", "failed", t)
                _polyLineData.value = emptyList()
            }
        }
    }


    fun postCourse(
        accessToken: String,
        keywords: List<String>,
        townId: Int?,
        provinceId: Int,
        cityId: Int?,
        imagePart: MultipartBody.Part?,
        onComplete: (Boolean, Int?) -> Unit
    ) {
        viewModelScope.launch {
            // 사전검증
            val pins = pinList.value
            val path = polyLineData.value
            if (pins.size < 2 || path.isEmpty()) {
                onComplete(false, null)
                return@launch
            }

            val resp = runCatching {
                courseRepo.createCourse(
                    course = courseData.value,    // time은 이미 분 단위로 세팅됨
                    pins = pins,
                    keywords = keywords,
                    regionsTownId = townId,
                    regionProvinceId = provinceId,
                    regionsCityId = cityId,
                    path = path,
                    accessToken = accessToken,
                    imagePart = imagePart
                )
            }.getOrElse {
                Log.e("COURSE_CREATE", "request failed", it)
                onComplete(false, null)
                return@launch
            }

            if (resp.isSuccessful) {
                val createdId = resp.body()?.result?.courseId
                if (createdId != null) {
                    onComplete(true, createdId)
                } else {
                    onComplete(false, null)
                }
            } else {
                onComplete(false, null)
            }
        }
    }

    // UI 상태(선택)
    sealed interface DetailUiState {
        data object Idle: DetailUiState
        data object Loading: DetailUiState
        data class Success(val id:Int): DetailUiState
        data class Error(val message:String): DetailUiState
    }
    private val _detailState = MutableStateFlow<DetailUiState>(DetailUiState.Idle)
    val detailState: StateFlow<DetailUiState> = _detailState

    fun loadCourseDetail(accessToken: String, courseId: Int) {
        if (detailHydrated.value && loadedCourseId.value == courseId) return

        viewModelScope.launch {
            _detailState.value = DetailUiState.Loading
            runCatching {
                fetchRepo.getCourseDetail(courseId, accessToken)
            }.onSuccess { dto ->
                hydrateFromDetail(dto)
                _loadedCourseId.value = dto.id
                _detailHydrated.value = true
                _detailState.value = DetailUiState.Success(dto.id)
            }.onFailure { t ->
                _detailState.value = DetailUiState.Error(t.message ?: "load failed")
            }
        }
    }

    fun setMode(newMode: CourseMode, courseId: Int?) {
        _mode.value = newMode
        _loadedCourseId.value = courseId
    }

    private fun hydrateFromDetail(dto: CourseDetailResponse) {
        // 1) 코스 메타
        _courseData.value = CourseData(
            title = dto.name,
            keyword = dto.keywords.firstOrNull(), // 기존 구조 유지(단일 keyword 사용 중이면)
            info = dto.description,
            time = dto.time   // 이미 분 단위
        )

        // 2) 핀 목록
        // 서버 핀을 그대로 PinItem으로 받는 구조라면 index 정규화만
        val normalized = dto.pins.mapIndexed { idx, p ->
            p.copy(index = idx + 1)
        }
        _pinList.value = normalized

        // 3) 경로
        val path: List<LatLng> = when {
            !dto.overviewPolyline.isNullOrBlank() -> decodeOverviewPolyline(dto.overviewPolyline)
            else -> emptyList() // 서버가 폴리라인을 안 줄 수도 있음
        }
        _polyLineData.value = path
    }

    private fun decodeOverviewPolyline(encoded: String?): List<LatLng> {
        if (encoded.isNullOrBlank()) return emptyList()
        return try { PolyUtil.decode(encoded) } catch (_: Exception) { emptyList() }
    }

    // 1) 코스 메타 교체
    fun overrideCourseMeta(cd: CourseData) {
        _courseData.value = cd
    }

    // 2) UPDATE 시그니처(바디는 스키마 확정 후)
    fun updateCourse(
        accessToken: String,
        courseId: Int,
        keywords: List<String>,
        townId: Int?,
        provinceId: Int,
        cityId: Int?,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val pins = pinList.value
            val path = polyLineData.value

            // 기본 검증: 핀 >= 2, 경로 존재
            if (pins.size < 2 || path.isEmpty()) {
                Log.w("COURSE_UPDATE", "invalid payload: pins=${pins.size}, path=${path.size}")
                onComplete(false)
                return@launch
            }

            val resp = runCatching {
                courseRepo.updateCourse(
                    courseId = courseId,
                    course = courseData.value,   // title/description/time 등 현재 편집 값
                    pins = pins,                 // 현재 핀 목록
                    keywords = keywords,         // 단일 키워드를 List로 만들어 전달
                    regionsTownId = townId,
                    regionProvinceId = provinceId,
                    regionsCityId = cityId,
                    path = path,                 // 현재 경로(인코딩은 repo에서 처리)
                    accessToken = accessToken
                )
            }.getOrElse { t ->
                Log.e("COURSE_UPDATE", "request failed", t)
                onComplete(false)
                return@launch
            }

            if (!resp.isSuccessful) {
                Log.e("COURSE_UPDATE", "http error: code=${resp.code()}")
                onComplete(false)
                return@launch
            }

            val success = resp.body()?.success == true
            if (!success) {
                Log.e("COURSE_UPDATE", "api success=false, body=${resp.body()}")
            }
            onComplete(success)
        }
    }
}
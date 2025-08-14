package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.with_runn.data.NeighborhoodPreviewResponse
import com.with_runn.data.RisingPreviewResponse
import com.with_runn.data.TokenManager
import com.with_runn.data.repository.CourseRepository
import kotlinx.coroutines.launch

private const val TAG = "WalkCourseVM"

class WalkCourseViewModel : ViewModel() {

    private val repository = CourseRepository()

    // 원본 미리보기 LiveData (UI에서 observe)
    private val _neighborhoodPreview = MutableLiveData<List<NeighborhoodPreviewResponse>>()
    val neighborhoodPreview: LiveData<List<NeighborhoodPreviewResponse>> = _neighborhoodPreview

    private val _risingPreview = MutableLiveData<List<RisingPreviewResponse>>()
    val risingPreview: LiveData<List<RisingPreviewResponse>> = _risingPreview

    // 로딩/중복호출 가드
    private var isLoadingNearby = false
    private var isLoadingRising = false

    // 같은 파라미터로의 재호출 방지용 키(성공 시 갱신)
    private var lastNearbyKey: Triple<Int, Int?, Int?>? = null

    private fun getToken(): String {
        val raw = TokenManager.getAccessToken()
        Log.d(TAG, "rawToken(len=${raw?.length}): ${raw?.take(12)}...")
        return "Bearer ${raw ?: ""}"
    }

    /** 우리동네 미리보기 */
    fun fetchNeighborhoodPreview(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null
    ) {
        if (provinceId <= 0) {
            Log.w(TAG, "[NEARBY] skip: invalid provinceId=$provinceId")
            return
        }
        if (isLoadingNearby) {
            Log.d(TAG, "[NEARBY] skip: already loading")
            return
        }
        // 같은 파라미터로 이미 성공하여 보유 중이면 생략
        val currentKey = Triple(provinceId, cityId, townId)
        if (lastNearbyKey == currentKey && !_neighborhoodPreview.value.isNullOrEmpty()) {
            Log.d(TAG, "[NEARBY] skip: same params & already loaded (key=$currentKey)")
            return
        }

        viewModelScope.launch {
            isLoadingNearby = true
            try {
                val token = getToken()
                Log.d(TAG, "[NEARBY] req → p=$provinceId c=$cityId t=$townId")
                val resp = repository.getNeighborhoodPreview(token, provinceId, cityId, townId)
                Log.d(TAG, "[NEARBY] code=${resp.code()} ok=${resp.isSuccessful}")

                if (resp.isSuccessful) {
                    val body = resp.body().orEmpty()
                    Log.d(TAG, "[NEARBY] body.size=${body.size} sample=${body.firstOrNull()}")
                    _neighborhoodPreview.value = body           // ✅ 성공시에만 갱신
                    lastNearbyKey = currentKey                  // ✅ 성공 시에만 키 갱신
                } else {
                    Log.e(TAG, "[NEARBY] fail=${resp.code()} err=${resp.errorBody()?.string()}")
                    // 실패 시엔 기존 데이터 유지 (사라짐 방지)
                }
            } catch (e: Exception) {
                Log.e(TAG, "[NEARBY] exception=${e.message}", e)
                // 실패 시엔 기존 데이터 유지
            } finally {
                isLoadingNearby = false
            }
        }
    }

    /** 떠오르는 코스 미리보기 */
    fun fetchRisingPreview() {
        if (isLoadingRising) {
            Log.d(TAG, "[RISING] skip: already loading")
            return
        }
        viewModelScope.launch {
            isLoadingRising = true
            try {
                val result = repository.getRisingPreview(getToken())
                Log.d(TAG, "[RISING] result.size=${result?.size ?: 0} sample=${result?.firstOrNull()}")
                _risingPreview.value = result ?: emptyList()   // 성공/빈값 모두 반영
            } catch (e: Exception) {
                Log.e(TAG, "[RISING] exception=${e.message}", e)
                // 실패 시엔 기존 데이터 유지
            } finally {
                isLoadingRising = false
            }
        }
    }

    /** 홈 프리뷰 보장: 내부 LiveData가 비어 있을 때만 호출 */
    fun ensureHomePreviews(provinceId: Int) {
        if (_neighborhoodPreview.value.isNullOrEmpty()) fetchNeighborhoodPreview(provinceId)
        if (_risingPreview.value.isNullOrEmpty()) fetchRisingPreview()
    }
}

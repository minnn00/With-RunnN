package com.with_runn.mapData

import com.with_runn.data.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MapSearchRepository(private val api: MapSearchApi) {

    data class CacheKey(
        val type: String,
        val province: String,
        val city: String?,
        val town: String?
    )

    private val _cacheFlow = MutableStateFlow<Map<CacheKey, List<MapSearchItem>>>(emptyMap())
    val cacheFlow: StateFlow<Map<CacheKey, List<MapSearchItem>>> = _cacheFlow

    fun flowFor(type: String, province: String, city: String? = null, town: String? = null) =
        _cacheFlow.map { it[CacheKey(type, province, city, town)].orEmpty() }
            .distinctUntilChanged()

    fun getCached(type: String, province: String, city: String? = null, town: String? = null) =
        _cacheFlow.value[CacheKey(type, province, city, town)].orEmpty()

    /**
     * 서버에서 페이지 단위로 모두 가져와 캐시에 적재.
     * size: 1..100 권장, 기본 50
     */
    suspend fun loadAllAndCache(
        type: String,
        province: String,
        city: String? = null,
        town: String? = null,
        pageSize: Int = 50
    ): List<MapSearchItem> = withContext(Dispatchers.IO) {
        require(type.isNotBlank()) { "type is required" }
        require(province.isNotBlank()) { "province is required" }
        require(pageSize in 1..100) { "size must be 1..100" }

        val key = CacheKey(type, province, city, town)
        val token = TokenManager.getAccessToken().orEmpty()
        val bearer = if (token.isNotBlank()) "Bearer $token" else ""

        val aggregated = mutableListOf<MapSearchItem>()
        var page = 0

        while (true) {
            val resp = api.searchPlaces(
                accessToken = bearer,
                type = type,
                province = province,
                city = city,
                town = town,
                page = page,
                size = pageSize
            )

            val batch = resp.result.content
            if (batch.isEmpty()) break

            aggregated += batch

            _cacheFlow.value = _cacheFlow.value.toMutableMap().apply {
                put(key, aggregated.toList())
            }

            if (resp.result.last) break
            page += 1
        }

        // 캐시 반영 (빈 리스트여도 기록하여 Empty 상태를 구독 가능하게 함)
        _cacheFlow.value = _cacheFlow.value.toMutableMap().apply {
            put(key, aggregated)
        }
        aggregated
    }
}
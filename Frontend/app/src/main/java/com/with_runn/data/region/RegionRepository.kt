package com.with_runn.data.region

class RegionRepository(
    private val api: RegionApi
) {

    suspend fun getProvinces(token: String): List<RegionResponse> {
        return api.getProvinces("Bearer $token")
    }

    suspend fun getCities(token: String, provinceId: Int): List<RegionResponse> {
        return api.getCities("Bearer $token", provinceId)
    }

    suspend fun getTowns(token: String, cityId: Int): List<RegionResponse> {
        return api.getTowns("Bearer $token", cityId)
    }

    suspend fun saveUserLocation(token: String, provinceId: Int?, cityId: Int?, townId: Int?) =
        api.saveUserLocation("Bearer $token", SaveRegionRequest(provinceId, cityId, townId))
}
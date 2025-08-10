package com.with_runn.data.region

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface RegionApi {
    @GET("api/region/province")
    suspend fun getProvinces(
        @Header("Authorization") accessToken: String
    ): List<RegionResponse>

    @GET("api/region/city")
    suspend fun getCities(
        @Header("Authorization") accessToken: String,
        @Query("provinceId") provinceId: Int
    ): List<RegionResponse>

    @GET("api/region/town")
    suspend fun getTowns(
        @Header("Authorization") accessToken: String,
        @Query("cityId") cityId: Int
    ): List<RegionResponse>

    @POST("api/users/region")
    suspend fun saveUserLocation(
        @Header("Authorization") accessToken: String,
        @Body body: SaveRegionRequest
    ): SaveRegionResponse
}
package com.with_runn.mapData

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MapSearchApi {
    @GET("api/maps/search/categories")
    suspend fun searchPlaces(
        @Header("Authorization") accessToken: String,
        @Query("type") type: String,                         // required
        @Query("region_province") province: String,          // required
        @Query("region_city") city: String? = null,          // optional
        @Query("region_town") town: String? = null,          // optional
        @Query("page") page: Int,                            // 0-based
        @Query("size") size: Int                             // 1..100
    ): MapSearchResponse
}
package com.with_runn.mapData

import PetFacilityResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface PetFacilityService {
    @GET("15111389/v1/uddi:41944402-8249-4e45-9e9d-a52d0a7db1cc")
    suspend fun getFacilities(
        @Query("serviceKey", encoded = true) apiKey: String,
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 1000,
        @Query("returnType") returnType: String = "JSON"  // 반드시 대문자 JSON
    ): PetFacilityResponse

    @GET("15111389/v1/uddi:41944402-8249-4e45-9e9d-a52d0a7db1cc")
    suspend fun getFacilitiesRaw(
        @Query("serviceKey", encoded = true) apiKey: String,
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 100,
        @Query("returnType") returnType: String = "JSON"
    ): ResponseBody
}
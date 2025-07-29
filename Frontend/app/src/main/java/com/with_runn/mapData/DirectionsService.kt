package com.with_runn.mapData

import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {
    @GET("directions/json")
    suspend fun getWalkingRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String?,
        @Query("mode") mode: String = "walking",
        @Query("key") apiKey: String
    ): DirectionsResponse
}

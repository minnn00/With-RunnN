package com.with_runn.tmap

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface TmapDirectionsService {
    @POST("tmap/routes/pedestrian")
    suspend fun getPedestrianRoute(
        @Header("appKey") appKey: String,
        @Query("version") version: Int = 1,
        @Body body: PedestrianRouteRequest
    ): TmapGeoJsonResponse
}
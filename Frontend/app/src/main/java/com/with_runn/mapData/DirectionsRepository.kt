package com.with_runn.mapData

import android.util.Log
import com.with_runn.BuildConfig
import com.with_runn.ui.course_edit.PinItem

class DirectionsRepository {
    suspend fun getWalkingRouteFromPins(pinList: List<PinItem>): DirectionsResponse? {
        if (pinList.size < 2) return null

        val origin = "${pinList.first().lat},${pinList.first().lng}"
        val destination = "${pinList.last().lat},${pinList.last().lng}"
        val waypoints = if (pinList.size > 2) {
            pinList.subList(1, pinList.size - 1)
                .joinToString("|") { "via:${it.lat},${it.lng}" }
        } else null

        // ✅ 최종 URL 확인용 로그
        val urlPreview = buildString {
            append("https://maps.googleapis.com/maps/api/directions/json?")
            append("origin=$origin&")
            if (!waypoints.isNullOrBlank()) append("waypoints=$waypoints&")
            append("destination=$destination&")
            append("mode=walking&")
            append("key=${BuildConfig.GOOGLE_MAP_API_KEY}")
        }

        Log.e("DIRECTIONS", "FINAL URL = $urlPreview")


        return DirectionRetrofitInstance.directionsService.getWalkingRoute(
            origin = origin,
            destination = destination,
            waypoints = waypoints,
            apiKey = BuildConfig.GOOGLE_MAP_API_KEY
        )
    }
}
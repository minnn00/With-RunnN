package com.with_runn.tmap

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonArray
import com.with_runn.tmap.TmapDirectionsRepository.RouteResult
import com.with_runn.ui.course_edit.PinItem

class TmapDirectionsRepository(
    private val service: TmapDirectionsService,
    private val appKey: String
) {
    data class RouteResult(
        val path: List<LatLng>,        // GoogleMap Polyline에 바로 넣을 좌표 (lat, lng)
        val totalDistanceMeters: Int?, // 총거리
        val totalTimeSeconds: Int?     // 총시간
    )

    /**
     * PinItem 리스트(순서대로) → 도보 경로
     * 최소 2개(출발/도착). 중간은 경유지(passList)로 연결.
     */
    suspend fun getWalkingRouteFromPins(pins: List<PinItem>): RouteResult {
        require(pins.size >= 2) { "At least 2 pins required" }

        val start = pins.first()
        val end = pins.last()
        val via = pins.drop(1).dropLast(1)

        val passList = if (via.isNotEmpty()) {
            via.joinToString("_") { "${it.lng},${it.lat}" } // X=lon, Y=lat
        } else null

        val req = PedestrianRouteRequest(
            startX = start.lng, startY = start.lat,
            endX = end.lng, endY = end.lat,
            passList = passList,
            startName = start.name.ifBlank { "출발지" },
            endName = end.name.ifBlank { "도착지" }
        )

        val resp = service.getPedestrianRoute(appKey = appKey, body = req)
        return resp.toRouteResult()
    }

    /**
     * 임의 좌표 입력 버전
     */
    suspend fun getWalkingRoute(
        start: LatLng,
        end: LatLng,
        waypoints: List<LatLng> = emptyList(),
        startName: String = "출발지",
        endName: String = "도착지"
    ): RouteResult {
        val passList = if (waypoints.isNotEmpty()) {
            waypoints.joinToString("_") { "${it.longitude},${it.latitude}" }
        } else null

        val req = PedestrianRouteRequest(
            startX = start.longitude, startY = start.latitude,
            endX = end.longitude, endY = end.latitude,
            passList = passList,
            startName = startName,
            endName = endName
        )

        val resp = service.getPedestrianRoute(appKey = appKey, body = req)
        return resp.toRouteResult()
    }
}

// --- Mapper ---
private fun TmapGeoJsonResponse.toRouteResult(): TmapDirectionsRepository.RouteResult {
    val path = ArrayList<LatLng>(1024)

    features.forEach { f ->
        val g = f.geometry ?: return@forEach
        val coords = g.coordinates ?: return@forEach

        when (g.type?.lowercase()) {
            "linestring" -> {
                if (coords.isJsonArray) {
                    appendLineStringArray(coords.asJsonArray, path)
                } else if (coords.isJsonPrimitive && coords.asJsonPrimitive.isString) {
                    appendLineStringString(coords.asString, path)
                }
            }
            "multilinestring" -> {
                if (coords.isJsonArray) {
                    coords.asJsonArray.forEach { sub ->
                        if (sub is com.google.gson.JsonArray) appendLineStringArray(sub, path)
                    }
                }
            }
            // "point" 는 경로선에 포함하지 않음
        }
    }

    val totalDistance = features.firstNotNullOfOrNull { it.properties?.totalDistance?.toIntOrNull() }
    val totalTime = features.firstNotNullOfOrNull { it.properties?.totalTime?.toIntOrNull() }

    return TmapDirectionsRepository.RouteResult(
        path = path,
        totalDistanceMeters = totalDistance,
        totalTimeSeconds = totalTime
    )
}

private fun appendLineStringArray(arr: JsonArray, out: MutableList<LatLng>) {
    arr.forEach { pairElem ->
        if (pairElem is JsonArray && pairElem.size() >= 2) {
            val lon = pairElem[0].asDouble
            val lat = pairElem[1].asDouble
            out.add(LatLng(lat, lon))
        }
    }
}

private fun appendLineStringString(s: String, out: MutableList<LatLng>) {
    // "lon,lat lon,lat ..." 형태
    s.split(" ").forEach { token ->
        val xy = token.split(",")
        if (xy.size >= 2) {
            val lon = xy[0].toDoubleOrNull()
            val lat = xy[1].toDoubleOrNull()
            if (lat != null && lon != null) out.add(LatLng(lat, lon))
        }
    }
}
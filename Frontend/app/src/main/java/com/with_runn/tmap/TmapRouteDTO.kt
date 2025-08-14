package com.with_runn.tmap

import com.google.gson.JsonElement

// --- Request ---
data class PedestrianRouteRequest(
    // X=lon, Y=lat (WGS84GEO)
    val startX: Double,
    val startY: Double,
    val endX: Double,
    val endY: Double,

    // 경유지: "x1,y1_x2,y2" 형식 (미사용 시 null)
    val passList: String? = null,

    // 좌표계 (Tmap 권장 기본값)
    val reqCoordType: String = "WGS84GEO",
    val resCoordType: String = "WGS84GEO",

    // 표기용 명칭 (POST 바디라 URL 인코딩 불필요)
    val startName: String = "출발지",
    val endName: String = "도착지",

    // 선택 파라미터들 (필요 시 사용)
    val sort: String? = "index",   // 안내점 정렬 옵션
    val angle: Int? = null,        // 출발 각도
    val speed: Int? = null,        // 보행 속도(m/s)
    val searchOption: String? = null // 탐색 옵션(없어도 됨)
)

// --- Response (GeoJSON 최소 파싱) ---
data class TmapGeoJsonResponse(
    val type: String? = null,
    val features: List<Feature> = emptyList()
)

data class Feature(
    val type: String? = null,
    val geometry: Geometry? = null,
    val properties: Properties? = null
)

data class Geometry(
    val type: String? = null,
    val coordinates: JsonElement? = null   // ← 핵심: 다형 수용
)

data class Properties(
    val index: String? = null,
    val pointIndex: String? = null,
    val name: String? = null,
    val description: String? = null,

    // 요약값
    val totalDistance: String? = null, // "1234" 또는 ""
    val totalTime: String? = null,     // "567"  또는 ""

    // 세부 구간
    val time: String? = null,
    val distance: String? = null,
    val roadType: String? = null,
    val turnType: String? = null,
    val facilityType: String? = null,

    // (있을 수 있음) 포인트 타입 정보
    val pointType: String? = null
)
package com.with_runn.data.course

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.with_runn.ui.course_edit.CourseData
import com.with_runn.ui.course_edit.PinItem
import okhttp3.ResponseBody
import retrofit2.Response

class CourseRepository(
    private val api: CourseApi = CourseService.api
) {
    suspend fun createCourse(
        course: CourseData,
        pins: List<PinItem>,
        keywords: List<String>,
        regions: List<String>,
        regionsData: List<RegionDataPayload>,
        userId: Int,
        regionProvinceId: Int,
        regionsCityId: Int,
        path: List<LatLng>,
        accessToken: String
    ): Response<CreateCourseResponse> {

        // 1) 경로 인코딩 (Google Encoded Polyline)
        val encoded = encodeOverviewPolyline(path, 250)

        // 2) 핀 변환 (색 정보 없으면 null)
        val pinPayloads = pins.map { p ->
            PinPayload(
                name = p.name,
                detail = p.content,
                color = null,
                latitude = p.lat,
                longitude = p.lng,
                pinOrder = p.index // 1-based 유지
            )
        }

        // 3) 요청 바디
        val body = CreateCourseRequest(
            name = course.title,
            description = course.info,
            time = course.time,                 // 이미 분 단위
            keywords = keywords,
            regions = regions,
            pins = pinPayloads,
            regionsData = regionsData,
            userId = userId,
            regionProvinceId = regionProvinceId,
            regionsCityId = regionsCityId,
            overviewPolyline = encoded
        )

        // 4) 토큰 보정 후 호출
        val headerToken = normalizeBearer(accessToken)
        return api.createCourse(headerToken, body)
    }

    private fun normalizeBearer(token: String): String =
        if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"

    private fun encodeOverviewPolyline(
        original: List<LatLng>,
        maxLen: Int
    ): String {
        if (original.isEmpty()) return ""

        // 1) 우선 원본 인코딩 시도
        var simplified = original
        var encoded = PolyUtil.encode(simplified)
        if (encoded.length <= maxLen) return encoded

        // 2) Douglas–Peucker로 점점 강하게 단순화 (단위: meters)
        var tol = 5.0
        while (encoded.length > maxLen && tol <= 200.0) {
            simplified = PolyUtil.simplify(original, tol)
            encoded = PolyUtil.encode(simplified)
            tol *= 1.6   // 5 → 8 → 13 → 21 → 34 → 54 → 86 → 138 → 221 (m)
        }

        // 3) 그래도 길면 균등 다운샘플(안전망)
        if (encoded.length > maxLen) {
            val targetPoints = (maxLen / 6).coerceAtLeast(10) // 대략 문자/점수 감으로 축소
            val step = (simplified.size / targetPoints).coerceAtLeast(2)
            val down = buildList {
                simplified.forEachIndexed { i, p -> if (i % step == 0) add(p) }
                if (lastOrNull() != simplified.last()) add(simplified.last())
            }
            encoded = PolyUtil.encode(down)
        }
        return encoded
    }
}
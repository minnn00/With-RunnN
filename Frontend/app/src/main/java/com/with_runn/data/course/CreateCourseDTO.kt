package com.with_runn.data.course

// 요청 바디
data class CreateCourseRequest(
    val name: String,
    val description: String?,
    val time: Int,
    val keywords: List<String>,
    val pins: List<PinPayload>,
    val regionProvinceId: Int,
    val regionsCityId: Int?,
    val regionsTownId: Int?,
    val courseImg: String? = null,
    val overviewPolyline: String
)

data class PinPayload(
    val name: String,
    val detail: String,
    val color: String?,
    val latitude: Double,
    val longitude: Double,
    val pinOrder: Int
)

data class RegionDataPayload(
    val id: Int,
    val name: String
)

data class CreateCourseResponse(
    val code: String,
    val message: String,
    val result: CreateCourseResult,
    val success: Boolean
)

data class CreateCourseResult(
    val courseId: Int,
    val overviewPolyline: String
)
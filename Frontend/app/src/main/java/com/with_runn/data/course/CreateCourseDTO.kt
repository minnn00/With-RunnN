package com.with_runn.data.course

import com.google.gson.annotations.SerializedName

// 요청 바디
data class CreateCourseRequest(
    val name: String,
    val description: String?,
    val time: Int,

    @SerializedName("keyWords")
    val keywords: List<String>,
    val regions: List<String>,
    val pins: List<PinPayload>,
    val regionsData: List<RegionDataPayload>,
    val userId: Int,
    val regionProvinceId: Int,
    val regionsCityId: Int,
    val overviewPolyline: String
)

data class PinPayload(
    val name: String,
    val detail: String?,
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
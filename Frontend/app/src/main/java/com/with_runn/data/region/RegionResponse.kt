package com.with_runn.data.region

data class RegionResponse(
    val id: Int,
    val name: String
)

// 저장 요청 DTO
data class SaveRegionRequest(
    val provinceId: Int?,
    val cityId: Int?,
    val townId: Int?
)

// 저장 응답 DTO
data class SaveRegionResponse(
    val code: String?,
    val message: String?,
    val success: Boolean
)

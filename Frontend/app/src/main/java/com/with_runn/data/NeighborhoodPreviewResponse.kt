package com.with_runn.data

import com.google.gson.annotations.SerializedName

data class NeighborhoodPreviewResponse(
    val courseId: Int,
    val name: String,
    val keyword: List<String>? = null,
    val keywords: List<String>? = null,
    val keyWords: List<String>? = null,
    val keywordText: String? = null,
    val time: Int?,               // 숫자로 (분)
    val courseImage: String?,     // 널 허용
    val location: String?,

    @SerializedName(
        value = "distanceMeters",
        alternate = ["distance_m", "distance_in_meters", "meters"]
    )
    val distanceMeters: Int? = null,

    @SerializedName(
    value = "distance",
    alternate = ["distanceStr", "courseDistance", "lengthText", "km"]
    )
    val distanceText: String? = null
)

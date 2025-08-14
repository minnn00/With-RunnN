package com.with_runn.data

import com.google.gson.annotations.SerializedName

data class RisingPreviewResponse(
    val courseId: Int,
    val name: String,
    val keyword: List<String>?,
    val courseImage: String?,   // 이미지 URL
    val time: String?,          // "30분" 또는 "00:30:00" 등
    val location: String?,

    // 서버가 줄 수도 있는 보조 필드들(없으면 null)
    val distance: String? = null,                // "1.3km" 등
    @SerializedName("distanceMeters") val distanceMeters: Int? = null, // 1300 등
    @SerializedName("durationMinutes") val durationMinutes: Int? = null
)
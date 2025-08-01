package com.with_runn.data

data class CourseDetailResponse(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val keywords: List<String>,
    val description: String,
    val time: String,
    val pins: List<Pin>,
    val isScrapped: Boolean,
    val isLiked: Boolean
)

data class Pin(
    val id: Int,
    val name: String,
    val color: String,
    val latitude: Double,
    val longitude: Double,
    val detail: String
)

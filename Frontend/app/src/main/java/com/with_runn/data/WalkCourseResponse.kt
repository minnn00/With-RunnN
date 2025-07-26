package com.with_runn.data

data class WalkCourseResponse(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val tags: List<String>,
    val distanceMeters: Int,
    val durationMinutes: Int,
    val isScrapped: Boolean,
    val isLiked: Boolean
)
package com.with_runn.data

data class LocalCourse(
    val id: Int,
    val title: String,
    val tag: String,
    val imageUrl: String?,
    val imageRes: Int,
    val distanceMeters: Int? = null,
    val durationMinutes: Int? = null
)
package com.with_runn

data class WalkCourse(
    val title: String,
    val tags: List<String>,
    val imageResId: Int,
    val distance: String,
    val time: String,
    val isScrapped: Boolean = false,
    val isLiked: Boolean = false
)

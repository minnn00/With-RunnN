package com.with_runn.data

data class HotCourse(
    val id: Int,
    val title: String,
    val tags: List<String>,
    val distance: String,
    val time: String,
    val imageRes: Int
)
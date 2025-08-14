package com.with_runn.data

data class RisingCourseResponse(
    val courseId: Int,
    val name: String,
    val keyword: List<String>,
    val time: String,
    val courseImage: String,
    val location: String
)
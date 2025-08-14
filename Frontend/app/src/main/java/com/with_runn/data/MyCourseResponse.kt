package com.with_runn.data

data class MyCourseResponse(
    val code: String,
    val message: String,
    val result: MyCourseResult,
    val success: Boolean
)

data class MyCourseResult(
    val myCourseList: List<MyCourse>
)

data class MyCourse(
    val courseId: Int,
    val courseName: String,
    val keyword: String,
    val time: String,
    val courseImage: String,
    val location: String,
    val createdAt: String,
    val distanceMeters: Int? = null,   // m 단위 숫자
    val distanceText: String? = null
)

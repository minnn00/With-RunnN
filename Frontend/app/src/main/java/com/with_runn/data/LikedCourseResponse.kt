package com.with_runn.data

data class LikedCoursesResponse(
    val code: String,
    val message: String,
    val result: LikeResult?
)

data class LikeResult(
    val likeList: List<LikedCourseItem>
)

data class LikedCourseItem(
    val courseId: Int,
    val count: Int,
    val likedAt: String
)
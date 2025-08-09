package com.with_runn.data


fun ScrapItem.toWalkCourseResponse(): WalkCourseResponse {
    return WalkCourseResponse(
        id = this.courseId,
        title = "",
        imageUrl = "",
        tags = emptyList(),
        distanceMeters = 0,
        durationMinutes = 0,
        isScrapped = true,
        isLiked = false
    )
}
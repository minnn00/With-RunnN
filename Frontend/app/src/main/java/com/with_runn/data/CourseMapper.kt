package com.with_runn.data


fun ScrapItem.toWalkCourseResponse(): WalkCourseResponse {
    return WalkCourseResponse(
        id = this.courseId,
        title = "", // 서버에서 안주므로 비워둠
        imageUrl = "",
        tags = emptyList(),
        distanceMeters = 0,
        durationMinutes = 0,
        isScrapped = true,
        isLiked = false
    )
}
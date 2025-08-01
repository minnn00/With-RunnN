package com.with_runn.data

import com.with_runn.data.toWalkCourse


fun WalkCourseResponse.toWalkCourse(): WalkCourse {
    return WalkCourse(
        title = this.title,
        tags = this.tags,
        imageResId = 0,
        distance = "${this.distanceMeters / 1000.0}km",
        time = "${this.durationMinutes}분",
        isScrapped = this.isScrapped,
        isLiked = this.isLiked
    )
}
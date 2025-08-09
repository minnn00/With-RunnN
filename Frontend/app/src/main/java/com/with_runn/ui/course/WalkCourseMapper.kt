package com.with_runn.ui.course

import com.with_runn.data.WalkCourse
import com.with_runn.data.WalkCourseResponse

fun WalkCourseResponse.toWalkCourse(): WalkCourse {
    return WalkCourse(
        id = 6,
        title = this.title,
        tags = this.tags,
        imageResId = 0,
        distance = "${this.distanceMeters / 1000.0}km",
        time = "${this.durationMinutes}분",
        isScrapped = this.isScrapped,
        isLiked = this.isLiked
    )
}
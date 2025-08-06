package com.with_runn.data

fun WalkCourseResponse.toWalkCourse(): WalkCourse {
    return WalkCourse(
        id = this.id,
        title = this.title,
        tags = this.tags,
        imageResId = 0, // 실제 이미지는 Glide 등에서 imageUrl 사용
        distance = "${this.distanceMeters / 1000.0}km",
        time = "${this.durationMinutes}분",
        isScrapped = this.isScrapped,
        isLiked = this.isLiked
    )
}

fun RisingPreviewResponse.toWalkCourse(): WalkCourse {
    return WalkCourse(
        id = this.courseId,
        title = this.name,
        tags = this.keyword,
        imageResId = 0,  // 실제 이미지 처리는 Glide 등으로 courseImage 사용
        distance = "",   // 거리 정보가 없으니 빈 문자열로
        time = this.time,
        isScrapped = false,
        isLiked = false
    )
}

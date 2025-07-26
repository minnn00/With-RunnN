package com.with_runn

class WalkCourseMapper {

    fun WalkCourseResponse.toWalkCourse(): WalkCourse {
        return WalkCourse(
            title = this.title,
            tags = this.tags,
            imageResId = 0, // 서버 이미지 URL이라서 resId는 더미값
            distance = "${this.distanceMeters / 1000.0}km",
            time = "${this.durationMinutes}분",
            isScrapped = this.isScrapped,
            isLiked = this.isLiked
        )
    }

}
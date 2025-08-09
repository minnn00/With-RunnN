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

fun MyCourse.toWalkCourse(): WalkCourse {
    return WalkCourse(
        id = courseId,
        title = courseName,
        tags = parseTags(keyword),
        imageResId = 0,
        imageUrl = courseImage,
        distance = "0km", // 서버에서 제공되지 않음 → 기본값 설정
        time = time.take(5), // "00:30:00" → "00:30"
        isScrapped = false,
        isLiked = false
    )
}

fun NeighborhoodPreviewResponse.toWalkCourse(): WalkCourse {
    return WalkCourse(
        id = this.courseId,
        title = this.name,          // name → title로 매핑
        tags = this.keyword,        // keyword → tags로 매핑
        imageResId = 0,              // Glide에서 courseImage로 로드
        imageUrl = this.courseImage, // 이미지 URL 지정
        distance = "",               // 서버에서 거리 안 주면 빈 값
        time = this.time,            // API time 그대로 사용
        isScrapped = false,          // 우리동네 미리보기엔 스크랩 여부 정보 없음
        isLiked = false              // 좋아요 여부 정보 없음
    )
}

package com.with_runn.data

// Int?(분) 또는 String?("HH:mm:ss"/"30분") → "N분"
private fun toMinuteString(raw: Any?): String? {
    return when (raw) {
        null -> null
        is Int -> if (raw > 0) "${raw}분" else null
        is String -> {
            val parts = raw.split(":")
            val minutes = if (parts.size == 3) {
                val h = parts[0].toIntOrNull() ?: 0
                val m = parts[1].toIntOrNull() ?: 0
                h * 60 + m
            } else {
                raw.filter { it.isDigit() }.toIntOrNull() ?: 0
            }
            if (minutes > 0) "${minutes}분" else null
        }
        else -> null
    }
}

// Int? meters → "x.xkm"
private fun Int?.toKmString(): String {
    val m = this ?: 0
    return if (m <= 0) "0km"                    // ← 0도 표시
    else if (m % 1000 == 0) "${m / 1000}km"
    else String.format("%.1fkm", m / 1000.0)
}
private fun Int?.toMinuteString(): String = if ((this ?: 0) > 0) "${this}분" else "0분"

// ───────── DTO → WalkCourse ─────────

fun NeighborhoodPreviewResponse.toWalkCourse(): WalkCourse =
    WalkCourse(
        id = courseId,
        title = name,
        tags = keyword ?: emptyList(),
        imageResId = 0,
        imageUrl = courseImage,
        distance = null,                 // 미제공
        time = toMinuteString(time),     // ← Int?든 String?이든 OK
        isScrapped = false,
        isLiked = false
    )

fun RisingCourseResponse.toWalkCourse(): WalkCourse =
    WalkCourse(
        id = courseId,
        title = name,
        tags = keyword ?: emptyList(),
        imageResId = 0,
        imageUrl = courseImage,
        distance = "0km",
        time = toMinuteString(time),     // "HH:mm:ss" → "N분"
        isScrapped = false,
        isLiked = false
    )

fun MyCourse.toWalkCourse(): WalkCourse =
    WalkCourse(
        id = courseId,
        title = courseName,
        tags = parseTags(keyword),
        imageResId = 0,
        imageUrl = courseImage,
        distance = null,
        time = toMinuteString(time),
        isScrapped = false,
        isLiked = false
    )

fun WalkCourseResponse.toWalkCourse(): WalkCourse =
    WalkCourse(
        id = id,
        title = title,
        tags = tags ?: emptyList(),
        imageResId = 0,
        imageUrl = imageUrl,
        distance = distanceMeters.toKmString(),     // 항상 "N.Nkm" or "0km"
        time = durationMinutes.toMinuteString(),    // 항상 "N분" or "0분"
        isScrapped = isScrapped,
        isLiked = isLiked
    )

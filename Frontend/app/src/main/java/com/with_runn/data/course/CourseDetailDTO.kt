package com.with_runn.data.course

import com.with_runn.ui.course_edit.PinItem

data class CourseDetailResponse(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val keywords: List<String>,
    val description: String,
    val time: Int,
    val pins: List<PinItem>,
    val overviewPolyline: String? = null,
    val isLiked : Boolean = false,
    val isScrapped: Boolean = false
)

data class CourseSummary(
    val courseId: Int,
    val name: String,
    val keyword: List<String>? = emptyList(),
    val time: Int,
    val courseImage: String? = null,
    val location: String? = null
)
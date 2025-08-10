package com.with_runn.data.course

import com.with_runn.ui.course_edit.PinItem

data class CourseDetailResponse(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val keywords: List<String>,
    val description: String,
    val time: Int,
    val pins: List<PinItem>
)
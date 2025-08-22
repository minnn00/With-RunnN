package com.with_runn.data.course

import java.io.Serializable

// 편집/생성 모드 식별
enum class CourseMode { CREATE, EDIT }

// Fragment arguments (Safe Args 쓰면 navArgs; 아니면 Bundle 직접)
data class CourseManageArgs(
    val mode: CourseMode,   // CREATE or EDIT
    val courseId: Int? = null
) : Serializable
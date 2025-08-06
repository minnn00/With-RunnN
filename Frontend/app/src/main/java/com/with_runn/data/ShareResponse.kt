package com.with_runn.data

data class ShareResponse(
    val userId: Int,
    val userName: String,
    val userProfileImage: String,
    val msg: String,
    val isCourse: Boolean,
    val courseId: Int,
    val createdAt: String,
    val courseImage: String,
    val courseTag: List<String>
)

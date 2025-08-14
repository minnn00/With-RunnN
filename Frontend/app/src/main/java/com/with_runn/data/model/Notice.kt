package com.with_runn.data.model

data class Notice(
    val id: Int,
    val message: String,
    val receiverId: Int,
    val receiverName: String,
    val actorId: Int,
    val actorName: String,
    val noticeType: String,
    val courseId: Int?,
    val courseName: String?
)

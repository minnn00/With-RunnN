package com.with_runn.data

data class ShareRequest(
    val courseId: Int,
    val friendIds: List<Int>
)
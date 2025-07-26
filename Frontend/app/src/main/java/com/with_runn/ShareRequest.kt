package com.with_runn.model.request

data class ShareRequest(
    val courseId: Int,
    val friendIds: List<Int>
)

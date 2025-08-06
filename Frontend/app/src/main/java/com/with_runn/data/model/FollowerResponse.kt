package com.with_runn.data.model

data class FollowerResponse(
    val code: String,
    val message: String,
    val result: FollowerResult,
    val success: Boolean
)

data class FollowerResult(
    val count: Int,
    val followers: List<Follower>
)



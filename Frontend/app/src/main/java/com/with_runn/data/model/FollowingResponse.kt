package com.with_runn.data.model

data class FollowingResponse(
    val code: String,
    val message: String,
    val result: FollowingResult,
    val success: Boolean
)

data class FollowingResult(
    val count: Int,
    val followings: List<Follower>
)

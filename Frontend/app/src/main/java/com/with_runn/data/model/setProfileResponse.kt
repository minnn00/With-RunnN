package com.with_runn.data.model

data class setProfileResponse(
    val code: String,
    val message: String,
    val result: setProfileRequest,
    val success: Boolean
)

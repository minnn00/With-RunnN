package com.with_runn.login

// LoginReq.kt
data class LoginReq(
    val email: String,
    val naverId: String
)

// LoginRes.kt
data class LoginRes(
    val memberId: Int,
    val accessToken: String
)

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val result: T?,
    val success: Boolean
)

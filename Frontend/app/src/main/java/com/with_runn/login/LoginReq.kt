package com.with_runn.login

import com.google.gson.annotations.SerializedName

// LoginReq.kt
data class LoginReq(
    val email: String,
    @SerializedName("loginId") val naverId: String
)

// LoginRes.kt
data class LoginRes(
    @SerializedName("userId") val memberId: Int,
    val accessToken: String,
    val newUser : Boolean
)

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val result: T?,
    val success: Boolean
)

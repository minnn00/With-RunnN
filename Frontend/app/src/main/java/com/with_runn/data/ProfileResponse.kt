package com.with_runn.data

data class ProfileResponse(
    val code: String,
    val message: String,
    val result: ProfileResult,
    val success: Boolean
)

data class ProfileResult(
    val id: Int,
    val userId: Int,
    val provinceId: Int,
    val provinceName: String,
    val cityId: Int,
    val cityName: String,
    val townId: Int,
    val townName: String,
    val name: String,
    val gender: String,
    val birth: String,
    val breed: String,
    val size: String,
    val profileImage: String,
    val character: String,
    val style: String
)

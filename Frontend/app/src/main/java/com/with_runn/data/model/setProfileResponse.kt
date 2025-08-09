package com.with_runn.data.model

data class setProfileResponse(
    val code: String,
    val message: String,
    val result: Result,
    val success: Boolean
)

data class Result(
    val provinceId: Int,
    val cityId: Int,
    val townId: Int,
    val name: String,
    val gender: String,
    val birth: String,
    val breed: String,
    val size: String,
    val characters: List<String>,
    val style: List<String>,
    val introduction: String
)

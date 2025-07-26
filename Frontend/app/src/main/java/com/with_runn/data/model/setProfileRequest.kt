package com.with_runn.data.model

data class setProfileRequest(
    val townId: Int,
    val cityId: Int,
    val name: String,
    val gender: String,
    val birth: String,
    val breed: String,
    val size: String,
    val characters: List<String>,
    val style: List<String>,
    val introduction: String
)

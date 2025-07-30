package com.with_runn.data

data class ScrapResponseWrapper(
    val code: String,
    val message: String,
    val result: ScrapResult,
    val success: Boolean
)
package com.with_runn.data

data class ScrapResponse(
    val code: String,
    val message: String,
    val result: ScrapResult?,
    val success: Boolean
)

data class ScrapResult(
    val scrapList: List<ScrapItem>
)

data class ScrapItem(
    val courseId: Int,
    val scrapedAt: String
)
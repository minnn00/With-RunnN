package com.with_runn.data

data class LocalCourse(
    val id: Int,
    val imageRes: Int,
    val tag: String,
    val title: String,
    val imageUrl: String? = null)
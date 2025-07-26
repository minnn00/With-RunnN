package com.with_runn.data

data class ShareTarget(
    val name: String,
    val imageResId: Int,
    var isSelected: Boolean = false // 선택 여부
)
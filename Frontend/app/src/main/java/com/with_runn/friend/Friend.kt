package com.with_runn.ui.friend

data class Friend(
    val name: String,
    val personalityTag: String = "",
    val personalityTags: List<String> = emptyList(),
    val imageResId: Int,
    val isSelected: Boolean = false
) 
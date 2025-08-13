package com.with_runn.data

data class HotCourse(
    val id: Int,
    val title: String,
    val tags: List<String> = emptyList(),
    val imageUrl: String? = null,   // ⬅️ URL로 변경
    val distance: String = "0km",   // ⬅️ "1.3km" 같은 포맷 문자열
    val time: String = "0분"        // ⬅️ "30분" 같은 포맷 문자열
)
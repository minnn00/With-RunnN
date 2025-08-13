package com.with_runn.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray

@Parcelize
data class WalkCourse(
    val id: Int,
    val title: String,
    val tags: List<String>,
    val imageResId: Int,
    val imageUrl: String? = null,
    val distance: String? = null,   // ⬅ String? 로 회귀 ("2.5km" 등)
    val time: String? = null,       // ⬅ String? 로 회귀 ("33분" 등)
    val isScrapped: Boolean = false,
    val isLiked: Boolean = false
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WalkCourse) return false
        return title == other.title &&
                imageResId == other.imageResId &&
                distance == other.distance &&
                time == other.time &&
                tags == other.tags
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + imageResId
        result = 31 * result + (distance?.hashCode() ?: 0)
        result = 31 * result + (time?.hashCode() ?: 0)
        result = 31 * result + tags.hashCode()
        return result
    }
}

fun parseTags(keyword: String): List<String> = try {
    if (keyword.startsWith("[")) {
        JSONArray(keyword).let { arr -> List(arr.length()) { i -> arr.getString(i) } }
    } else keyword.split(",").map { it.trim() }
} catch (_: Exception) { emptyList() }

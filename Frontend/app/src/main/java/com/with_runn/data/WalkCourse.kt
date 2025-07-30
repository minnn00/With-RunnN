package com.with_runn.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WalkCourse(
    val title: String,
    val tags: List<String>,
    val imageResId: Int,
    val distance: String,
    val time: String,
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
        result = 31 * result + distance.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + tags.hashCode()
        return result
    }
}

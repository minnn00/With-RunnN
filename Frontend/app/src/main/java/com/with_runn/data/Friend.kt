package com.with_runn.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Friend(
    val id: String,
    val name: String,
    val imageUrl: String,
    val tags: List<String>,
    val bio: String,
    val age: String,
    val ageInMonths: Int,
    val breed: String,
    val category: String,
    val personality: List<String>,
    val walkingStyle: List<String>,
    var isFollowing: Boolean = false,
    val gender: Gender = Gender.MALE
) : Parcelable

enum class Gender {
    MALE, FEMALE
} 
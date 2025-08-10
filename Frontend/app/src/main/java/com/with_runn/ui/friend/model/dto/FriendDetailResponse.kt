package com.with_runn.ui.friend.model.dto

import com.google.gson.annotations.SerializedName

data class FriendDetailResponse(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("profileImage")
    val profileImage: String?,
    @SerializedName("breed")
    val breed: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("age")
    val age: String?,
    @SerializedName("size")
    val size: String?,
    @SerializedName("introduction")
    val introduction: String?
) 
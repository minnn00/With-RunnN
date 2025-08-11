package com.with_runn.ui.friend.model.dto

import com.google.gson.annotations.SerializedName

data class RecommendedFriendResponse(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("profileImage")
    val profileImage: String?,
    @SerializedName("style")
    val style: List<String>?,
    @SerializedName("characters")
    val characters: List<String>?,
    @SerializedName("common")
    val common: List<String>?
) 
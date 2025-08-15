package com.with_runn.share

import com.google.gson.annotations.SerializedName

data class ShareCourseRequest(
    @SerializedName("isChat") val isChat: Boolean = true,
    @SerializedName("userId") val userId: Int,
    @SerializedName("chatId") val chatId: Int,
    @SerializedName("courseId") val courseId: Int
)
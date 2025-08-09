package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅 초대 가능한 사용자 DTO
 */
data class InviteUserDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("profileImage") val profileImage: String
) 
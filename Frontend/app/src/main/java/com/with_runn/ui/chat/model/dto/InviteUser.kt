package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 초대할 사용자 정보
 */
data class InviteUser(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("userId")
    val userId: Int
)
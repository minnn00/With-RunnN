package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 초대 목록 API 응답 래퍼
 */
data class InviteListResponse(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("result")
    val result: List<InviteUserDto>,
    
    @SerializedName("success")
    val success: Boolean
) 
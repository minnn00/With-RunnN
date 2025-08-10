package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅 메시지 목록 응답 DTO
 */
data class MessageListResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<MessageDto>,
    @SerializedName("success") val success: Boolean
) 
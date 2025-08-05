package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅방 목록 API 응답 래퍼
 */
data class ChatListResponse(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("result")
    val result: List<ChatRoomDto>,
    
    @SerializedName("success")
    val success: Boolean
) 
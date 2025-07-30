package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅방 이름 설정 API 응답 DTO
 */
data class UpdateChatNameResponse(
    @SerializedName("chatId")
    val chatId: Int,
    
    @SerializedName("name")
    val name: String
) 
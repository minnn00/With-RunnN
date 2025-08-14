package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅방 생성 요청 DTO
 */
data class CreateChatRequest(
    @SerializedName("userId")
    val userId: Int
) 
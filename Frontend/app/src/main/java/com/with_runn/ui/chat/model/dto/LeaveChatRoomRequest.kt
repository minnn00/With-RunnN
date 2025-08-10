package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅방 나가기 요청 DTO
 */
data class LeaveChatRoomRequest(
    @SerializedName("timestamp")
    val timestamp: Long
) 
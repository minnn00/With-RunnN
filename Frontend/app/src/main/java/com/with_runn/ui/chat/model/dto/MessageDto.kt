package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅 메시지 API 응답 DTO
 * api/chat/{chatId} 응답 구조에 맞춤
 */
data class MessageDto(
    @SerializedName("messageId")
    val messageId: Int,
    
    @SerializedName("sender")
    val sender: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("timestamp")
    val timestamp: String,
    
    @SerializedName("messageType")
    val messageType: String = "TEXT" // TEXT, SYSTEM, COURSE_SHARE 등
    
    // 추가 필드들 (API 명세서에 따라 확장 가능)
    // @SerializedName("senderProfile")
    // val senderProfile: String? = null,
    
    // @SerializedName("isRead")
    // val isRead: Boolean = false
) 
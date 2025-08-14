package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅 메시지 API 응답 DTO
 * 실제 API 응답 구조에 맞춤
 */
data class MessageDto(
    @SerializedName("userId")
    val userId: Int,
    
    @SerializedName("chatId")
    val chatId: Int,
    
    @SerializedName("userName")
    val userName: String,
    
    @SerializedName("userProfileImage")
    val userProfileImage: String?,
    
    @SerializedName("msg")
    val msg: String,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("course")
    val course: Boolean,

    @SerializedName("messageId")
    val messageId: Int?
) 
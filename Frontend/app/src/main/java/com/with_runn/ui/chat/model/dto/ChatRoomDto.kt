package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * API 응답용 ChatRoom DTO
 * api/chat/list 응답 구조에 맞춤
 */
data class ChatRoomDto(
    @SerializedName("chatId")
    val chatId: Int,
    
    @SerializedName("users")
    val users: List<String>,
    
    @SerializedName("userProfiles")
    val userProfiles: List<String>,
    
    @SerializedName("participants")
    val participants: Int,
    
    @SerializedName("lastMsgReceived")
    val lastMsgReceived: String,
    
    @SerializedName("lastMessage")
    val lastMessage: String,
    
    @SerializedName("notificationCount")
    val notificationCount: Int
) 
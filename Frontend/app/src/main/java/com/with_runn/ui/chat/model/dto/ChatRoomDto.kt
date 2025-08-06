package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * API 응답용 ChatRoom DTO
 * api/chat/list 응답 구조에 맞춤
 * unReadMsgCount는 BE에서 계산해서 제공
 */
data class ChatRoomDto(
    @SerializedName("chatId")
    val chatId: Int,
    
    @SerializedName("chatName")
    val chatName: String,
    
    @SerializedName("usernameList")
    val usernameList: List<String>?,
    
    @SerializedName("userProfileList")
    val userProfileList: List<String>?,
    
    @SerializedName("participants")
    val participants: Int,
    
    @SerializedName("lastReceivedMsg")
    val lastReceivedMsg: String?,
    
    @SerializedName("lastMessage")
    val lastMessage: String?,
    
    @SerializedName("unReadMsgCount")
    val unReadMsgCount: Int
) 
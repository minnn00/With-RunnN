package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * API 응답용 ChatRoom DTO
 * 실제 API 응답에 맞춤
 */
data class ChatRoomDto(
    @SerializedName("chatId")
    val chatId: Int,
    
    @SerializedName("chatName")
    val chatName: String?,
    
    @SerializedName("usernameList")
    val usernameList: List<String>?,
    
    @SerializedName("userProfileList")
    val userProfileList: List<String>?,
    
    @SerializedName("participants")
    val participants: Int,
    
    @SerializedName("lastReceivedMsg")
    val lastReceivedMsg: String?,
    
    @SerializedName("unReadMsgCount")
    val unReadMsgCount: Int
) 
package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅 메시지 API 응답 DTO
 * 새 API 명세서에 맞춤
 */
data class MessageDto(
    @SerializedName("chatId")
    val chatId: Int,
    
    @SerializedName("userId")
    val userId: Int,
    
    @SerializedName("userName")
    val userName: String,
    
    @SerializedName("userProfileImage")
    val userProfileImage: String,
    
    @SerializedName("msg")
    val msg: String,
    
    @SerializedName("isCourse")
    val isCourse: Boolean,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    // 코스 공유 메시지일 때만 사용되는 필드들
    @SerializedName("courseId")
    val courseId: Int? = null,
    
    @SerializedName("courseImage")
    val courseImage: String? = null,
    
    @SerializedName("courseTag")
    val courseTag: List<String>? = null
) 
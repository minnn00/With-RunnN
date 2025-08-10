package com.with_runn.ui.chat.model

data class ChatRoom(
    val chatId: Int,
    val name: String,
    val time: String,
    val lastMessage: String,
    val notificationCount: Int = 0,
    val participants: Int = 0,  // 참여자 수 추가
    val profileImageResId: Int = 0,
    val profileImage2ResId: Int = 0,
    val hasSecondImage: Boolean = false,
    val profileImageUrl: String? = null,
    val profileImage2Url: String? = null
) 
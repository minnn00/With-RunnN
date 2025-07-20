package com.with_runn

data class ChatRoom(
    val name: String,
    val time: String,
    val lastMessage: String,
    val notificationCount: Int = 0,
    val profileImageResId: Int = 0,
    val profileImage2ResId: Int = 0,
    val hasSecondImage: Boolean = false
) 
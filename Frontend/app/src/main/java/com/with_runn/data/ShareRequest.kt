package com.with_runn.data

data class ShareRequest(
    val isChat: Boolean,
    val userId: Int,
    val targetUserId: Int?,  // 친구에게 공유할 때 사용
    val chatId: Int?,        // 채팅방 공유 시 사용
    val courseId: Int
)

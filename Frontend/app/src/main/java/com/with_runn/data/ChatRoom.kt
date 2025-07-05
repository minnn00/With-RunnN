package com.with_runn.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatRoom(
    val id: String,
    val name: String,
    val profileImageUrl: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val participantIds: List<String> = emptyList()
) : Parcelable 
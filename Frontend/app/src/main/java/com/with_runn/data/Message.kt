package com.with_runn.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MessageType {
    SENT,
    RECEIVED,
    DATE_SEPARATOR,
    SYSTEM_MESSAGE
}

@Parcelize
data class Message(
    val id: String,
    val type: MessageType,
    val content: String,
    val senderId: String? = null,
    val senderName: String? = null,
    val senderProfileUrl: String? = null,
    val timestamp: Long,
    val timeString: String,
    val dateString: String? = null // 날짜 구분선용
) : Parcelable 
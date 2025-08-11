package com.with_runn.ui.chat.model.mapper

import com.with_runn.R
import java.text.SimpleDateFormat
import java.util.*
import com.with_runn.ui.chat.model.ChatRoom
import com.with_runn.ui.chat.model.dto.ChatRoomDto

/**
 * ChatRoomDto를 ChatRoom으로 변환하는 매퍼
 */
object ChatRoomMapper {
    
    /**
     * ChatRoomDto를 ChatRoom으로 변환
     */
    fun ChatRoomDto.toChatRoom(): ChatRoom {
        return ChatRoom(
            chatId = chatId,
            name = chatName ?: "",
            time = formatTime(lastReceivedMsg),
            lastMessage = "", // 새로운 API에서는 lastMessage가 별도로 제공되지 않음
            notificationCount = unReadMsgCount,
            participants = participants,
            profileImageResId = R.drawable.img_profile_default,
            profileImage2ResId = R.drawable.img_profile_default,
            hasSecondImage = (userProfileList?.size ?: 0) > 1,
            profileImageUrl = userProfileList?.firstOrNull(),
            profileImage2Url = if ((userProfileList?.size ?: 0) > 1) userProfileList?.get(1) else null
        )
    }
    
    /**
     * 날짜 문자열을 시간 형식으로 변환
     * "2025-07-07" -> "07.07"
     * null이나 빈 문자열인 경우 "방금 전" 반환
     */
    private fun formatTime(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "방금 전"
        
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            "방금 전"
        }
    }
} 
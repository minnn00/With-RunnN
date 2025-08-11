package com.with_runn.ui.chat.model.mapper

import com.with_runn.R
import com.with_runn.data.TokenManager
import java.text.SimpleDateFormat
import java.util.*
import com.with_runn.ui.chat.model.Message
import com.with_runn.ui.chat.model.dto.MessageDto

/**
 * MessageDto를 Message로 변환하는 매퍼
 */
object MessageMapper {
    
    /**
     * MessageDto를 Message로 변환
     * 실제 API 응답 구조에 맞춤
     */
    fun MessageDto.toMessage(): Message {
        val currentUserId = TokenManager.getCurrentUserId()
        return Message(
            messageId = userId, // userId를 messageId로 사용
            sender = userName,
            content = msg,
            timestamp = formatTimestamp(createdAt),
            isFromMe = userId == currentUserId, // 현재 사용자 ID와 비교
            isSystemMessage = false,
            isCourseShare = course,
            senderProfileResId = R.drawable.img_profile_default, // TODO: userProfileImage 사용
            messageType = if (course) Message.TYPE_COURSE_SHARE else Message.TYPE_TEXT
        )
    }
    
    /**
     * 타임스탬프 형식 변환
     * "2025-08-06T10:06:26.150615" → "오후 10:06"
     */
    private fun formatTimestamp(timestamp: String): String {
        return try {
            // 실제 API 응답의 타임스탬프 형식에 맞춰 수정
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)
            val date = inputFormat.parse(timestamp)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            // 파싱 실패 시 원본 반환
            timestamp
        }
    }
} 
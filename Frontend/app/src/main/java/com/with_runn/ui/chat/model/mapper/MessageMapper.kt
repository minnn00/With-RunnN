package com.with_runn.ui.chat.model.mapper

import com.with_runn.R
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
     * @param messageDto API 응답 DTO
     * @param currentUserId 현재 사용자 ID (내가 보낸 메시지 판별용)
     */
    fun MessageDto.toMessage(currentUserId: String = "나"): Message {
        return Message(
            messageId = messageId,
            sender = sender,
            content = content,
            timestamp = formatTimestamp(timestamp),
            isFromMe = sender == currentUserId,
            isSystemMessage = messageType == Message.TYPE_SYSTEM,
            isCourseShare = messageType == Message.TYPE_COURSE_SHARE,
            senderProfileResId = getProfileImageResId(sender),
            messageType = messageType
        )
    }
    
    /**
     * 타임스탬프 형식 변환
     * "2025-07-07T14:30:00" → "오후 2:30"
     */
    private fun formatTimestamp(timestamp: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)
            val date = inputFormat.parse(timestamp)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            // 파싱 실패 시 원본 반환
            timestamp
        }
    }
    
    /**
     * 발신자별 프로필 이미지 리소스 ID 반환
     */
    private fun getProfileImageResId(sender: String): Int {
        return when (sender) {
            "조니" -> R.drawable.jonny
            "마루" -> R.drawable.maru
            "이름 없는 사용자" -> R.drawable.guri
            "초코" -> R.drawable.ellipse_52
            "모찌" -> R.drawable.ellipse_50
            "나" -> R.drawable.img_profile_default
            else -> R.drawable.img_profile_default
        }
    }
} 
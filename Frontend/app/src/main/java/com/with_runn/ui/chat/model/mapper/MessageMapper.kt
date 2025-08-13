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
        val formattedTime = formatTimestamp(createdAt)

        // 시스템 메시지 패턴 감지 (초대/시스템 안내 등)
        val isSystemByPattern = try {
            val normalized = msg.trim()
            // 예: "A님이 B님을 초대하였습니다" 형태 감지
            val invitePattern = ".*님이 .*님을 초대하였습니다.*".toRegex()
            val createdPattern = ".*채팅방이 생성되었습니다.*".toRegex()
            normalized.contains("상대방과 나누는 첫 대화입니다") || invitePattern.matches(normalized) || createdPattern.matches(normalized)
        } catch (_: Exception) { false }

        val isSystemMessage = isSystemByPattern
        val isFromMeComputed = if (isSystemMessage) false else userId == currentUserId

        // 서버 messageId 우선 사용, 없으면 안전한 대체 키 생성
        val idForUi = messageId ?: (userId * 1_000_000 + (createdAt.hashCode() and 0x7fffffff))
        return Message(
            messageId = idForUi,
            sender = userName,
            content = msg,
            timestamp = formattedTime,
            isFromMe = isFromMeComputed,
            isSystemMessage = isSystemMessage,
            isCourseShare = course,
            senderProfileResId = R.drawable.img_profile_default,
            messageType = when {
                course -> Message.TYPE_COURSE_SHARE
                isSystemMessage -> Message.TYPE_SYSTEM
                else -> Message.TYPE_TEXT
            }
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
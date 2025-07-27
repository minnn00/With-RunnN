package com.with_runn.ui.chat.model

/**
 * UI에서 사용하는 메시지 모델
 * DTO와는 다르게 UI에 필요한 정보들이 추가됨
 */
data class Message(
    val messageId: Int,
    val sender: String,
    val content: String,
    val timestamp: String,
    val isFromMe: Boolean = false, // 내가 보낸 메시지인지 여부
    val isSystemMessage: Boolean = false, // 시스템 메시지인지 여부
    val isCourseShare: Boolean = false, // 코스 공유 메시지인지 여부
    val senderProfileResId: Int = 0, // 발신자 프로필 이미지 리소스 ID
    val messageType: String = "TEXT" // 메시지 타입
) {
    companion object {
        // 메시지 타입 상수
        const val TYPE_TEXT = "TEXT"
        const val TYPE_SYSTEM = "SYSTEM"
        const val TYPE_COURSE_SHARE = "COURSE_SHARE"
    }
} 
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
            name = generateChatName(users),
            time = formatTime(lastMsgReceived),
            lastMessage = lastMessage,
            notificationCount = notificationCount,
            profileImageResId = getProfileImageResId(userProfiles.firstOrNull()),
            profileImage2ResId = if (userProfiles.size > 1) getProfileImageResId(userProfiles[1]) else 0,
            hasSecondImage = userProfiles.size > 1
        )
    }
    
    /**
     * 채팅방 이름 생성
     * 단일 사용자: 사용자 이름
     * 그룹 채팅: 첫 번째, 두 번째 사용자 이름 (예: "초코, 모찌")
     */
    private fun generateChatName(users: List<String>): String {
        return when {
            users.isEmpty() -> "알 수 없음"
            users.size == 1 -> users[0]
            else -> "${users[0]}, ${users[1]}"
        }
    }
    
    /**
     * 날짜 문자열을 시간 형식으로 변환
     * "2025-07-07" -> "07.07"
     */
    private fun formatTime(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
    
    /**
     * 프로필 이미지 URL을 리소스 ID로 변환
     * 실제 구현에서는 URL을 기반으로 적절한 리소스 ID를 반환해야 함
     */
    private fun getProfileImageResId(profileUrl: String?): Int {
        return when (profileUrl) {
            "profile1" -> R.drawable.jonny
            "profile2" -> R.drawable.maru
            "profile3" -> R.drawable.guri
            "profile4" -> R.drawable.ellipse_52
            "profile5" -> R.drawable.ellipse_50
            else -> R.drawable.img_profile_default
        }
    }
} 
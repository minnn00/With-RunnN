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
            name = chatName ?: "알 수 없음", // Handle nullable chatName
            time = formatTime(lastReceivedMsg ?: ""),
            lastMessage = lastMessage ?: "", // 마지막 메시지 내용
            notificationCount = unReadMsgCount, // BE에서 계산된 unReadMsgCount 사용
            participants = participants, // 참여자 수 매핑
            profileImageResId = R.drawable.img_profile_default, // 기본 이미지
            profileImage2ResId = R.drawable.img_profile_default, // 기본 이미지
            hasSecondImage = (userProfileList?.size ?: 0) > 1,
            profileImageUrl = userProfileList?.firstOrNull(), // 실제 S3 URL
            profileImage2Url = if (userProfileList?.size ?: 0 > 1) userProfileList!![1] else null // 두 번째 S3 URL
        )
    }
    
    /**
     * 날짜 문자열을 시간 형식으로 변환
     * "2025-07-07" -> "07.07"
     * null이나 빈 문자열인 경우 "방금 전" 반환
     */
    private fun formatTime(dateString: String): String {
        if (dateString.isBlank()) return "방금 전"
        
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            "방금 전"
        }
    }
    
    /**
     * 프로필 이미지 URL을 리소스 ID로 변환
     * 실제 구현에서는 URL을 기반으로 적절한 리소스 ID를 반환해야 함
     */
    private fun getProfileImageResId(profileUrl: String?): Int? {
        if (profileUrl.isNullOrEmpty()) return null
        
        try {
            // URL 디코딩
            val decodedUrl = java.net.URLDecoder.decode(profileUrl, "UTF-8")
            
            return when {
                decodedUrl.contains("골든리트리버") -> R.drawable.jonny
                decodedUrl.contains("마루") -> R.drawable.maru
                decodedUrl.contains("구리") -> R.drawable.guri
                decodedUrl.contains("코코") -> R.drawable.ellipse_52
                decodedUrl.contains("해피") -> R.drawable.ellipse_50
                else -> {
                    // 디버깅을 위한 로그
                    android.util.Log.d("ChatRoomMapper", "매칭되지 않은 URL: $decodedUrl")
                    null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ChatRoomMapper", "URL 디코딩 실패: $profileUrl", e)
            return null
        }
    }
} 
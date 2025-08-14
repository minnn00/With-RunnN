package com.with_runn.ui.chat.model.mapper

import com.with_runn.R
import java.text.SimpleDateFormat
import java.util.*
import com.with_runn.ui.chat.model.ChatRoom
import com.with_runn.ui.chat.model.dto.ChatRoomDto
import android.util.Log

/**
 * ChatRoomDto를 ChatRoom으로 변환하는 매퍼
 */
object ChatRoomMapper {
    
    /**
     * ChatRoomDto를 ChatRoom으로 변환
     */
    fun ChatRoomDto.toChatRoom(): ChatRoom {
        // 🆕 디버깅을 위한 로그 추가
        Log.d("ChatRoomMapper", "ChatRoomDto 변환 시작:")
        Log.d("ChatRoomMapper", "  chatId: $chatId")
        Log.d("ChatRoomMapper", "  lastReceivedMsg: '$lastReceivedMsg'")
        Log.d("ChatRoomMapper", "  lastReceivedMsg 타입: ${lastReceivedMsg?.javaClass?.simpleName}")
        
        val formattedTime = formatTime(lastReceivedMsg)
        Log.d("ChatRoomMapper", "  포맷된 시간: '$formattedTime'")
        
        return ChatRoom(
            chatId = chatId,
            name = chatName ?: "",
            time = formattedTime,
            lastMessage = "", // 🆕 빈 문자열로 초기화 (WebSocket으로 실시간 업데이트)
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
     * "2025-08-10T12:05:48.832766145" -> "오후 12:05"
     * "2025-08-10T12:05:48" -> "오후 12:05"
     * "2025-08-10" -> "08.10"
     * null이나 빈 문자열인 경우 "방금 전" 반환
     */
    private fun formatTime(dateString: String?): String {
        if (dateString.isNullOrBlank()) {
            Log.d("ChatRoomMapper", "formatTime: null 또는 빈 문자열 -> '방금 전' 반환")
            return "방금 전"
        }
        
        Log.d("ChatRoomMapper", "formatTime 시작: '$dateString'")
        
        return try {
            // ISO 8601 형식 (2025-08-10T12:05:48.832766145) 시도
            if (dateString.contains("T")) {
                val date: Date?
                
                // 마이크로초가 있는 경우 (6자리)
                if (dateString.contains(".") && dateString.split(".")[1].length >= 6) {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
                    date = inputFormat.parse(dateString)
                    Log.d("ChatRoomMapper", "마이크로초 6자리 형식 파싱 시도")
                }
                // 마이크로초가 있는 경우 (3자리)
                else if (dateString.contains(".") && dateString.split(".")[1].length == 3) {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                    date = inputFormat.parse(dateString)
                    Log.d("ChatRoomMapper", "마이크로초 3자리 형식 파싱 시도")
                }
                // 마이크로초가 없는 경우
                else {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    date = inputFormat.parse(dateString)
                    Log.d("ChatRoomMapper", "마이크로초 없는 형식 파싱 시도")
                }
                
                if (date != null) {
                    val outputFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)
                    val result = outputFormat.format(date)
                    Log.d("ChatRoomMapper", "시간 파싱 성공: '$dateString' -> '$result'")
                    result
                } else {
                    Log.w("ChatRoomMapper", "날짜 파싱 실패: '$dateString'")
                    // 🆕 파싱 실패 시 원본 값 반환
                    dateString
                }
            } else {
                // 날짜만 있는 경우 (2025-08-10)
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                val outputFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
                val result = outputFormat.format(date ?: Date())
                Log.d("ChatRoomMapper", "날짜 파싱 성공: '$dateString' -> '$result'")
                result
            }
        } catch (e: Exception) {
            Log.e("ChatRoomMapper", "시간 파싱 실패: '$dateString'", e)
            // 🆕 파싱 실패 시 원본 값 반환 (디버깅용)
            "파싱실패:$dateString"
        }
    }
} 
package com.with_runn.ui.chat.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * 읽지 않은 메시지 수를 관리하는 클래스
 * SharedPreferences를 사용하여 로컬에 저장
 */
class UnreadMessageManager private constructor(context: Context) {
    
    companion object {
        private const val PREF_NAME = "unread_messages"
        private const val KEY_PREFIX = "unread_count_"
        
        @Volatile
        private var INSTANCE: UnreadMessageManager? = null
        
        fun getInstance(context: Context): UnreadMessageManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UnreadMessageManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * 특정 채팅방의 읽지 않은 메시지 수를 가져오기
     */
    fun getUnreadCount(chatId: Int): Int {
        return sharedPreferences.getInt("${KEY_PREFIX}${chatId}", 0)
    }
    
    /**
     * 특정 채팅방의 읽지 않은 메시지 수를 설정
     */
    fun setUnreadCount(chatId: Int, count: Int) {
        sharedPreferences.edit()
            .putInt("${KEY_PREFIX}${chatId}", count)
            .apply()
        Log.d("UnreadMessageManager", "채팅방 $chatId 읽지 않은 메시지 수 설정: $count")
    }
    
    /**
     * 특정 채팅방의 읽지 않은 메시지 수를 증가
     */
    fun incrementUnreadCount(chatId: Int) {
        val currentCount = getUnreadCount(chatId)
        val newCount = currentCount + 1
        setUnreadCount(chatId, newCount)
        Log.d("UnreadMessageManager", "채팅방 $chatId 읽지 않은 메시지 수 증가: $currentCount -> $newCount")
    }
    
    /**
     * 특정 채팅방의 읽지 않은 메시지 수를 리셋 (0으로 설정)
     */
    fun resetUnreadCount(chatId: Int) {
        setUnreadCount(chatId, 0)
        Log.d("UnreadMessageManager", "채팅방 $chatId 읽지 않은 메시지 수 리셋")
    }
    
    /**
     * 특정 채팅방의 읽지 않은 메시지 수를 삭제
     */
    fun clearUnreadCount(chatId: Int) {
        sharedPreferences.edit()
            .remove("${KEY_PREFIX}${chatId}")
            .apply()
        Log.d("UnreadMessageManager", "채팅방 $chatId 읽지 않은 메시지 수 삭제")
    }
    
    /**
     * 모든 채팅방의 읽지 않은 메시지 수를 삭제
     */
    fun clearAllUnreadCounts() {
        sharedPreferences.edit().clear().apply()
        Log.d("UnreadMessageManager", "모든 채팅방 읽지 않은 메시지 수 삭제")
    }
} 
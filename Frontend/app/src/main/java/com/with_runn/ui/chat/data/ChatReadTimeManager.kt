package com.with_runn.ui.chat.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * 채팅방 읽은 시간을 관리하는 클래스
 * SharedPreferences를 사용하여 로컬에 저장
 */
class ChatReadTimeManager private constructor(context: Context) {
    
    companion object {
        private const val PREF_NAME = "chat_read_time"
        private const val KEY_PREFIX = "chat_read_time_"
        
        @Volatile
        private var INSTANCE: ChatReadTimeManager? = null
        
        fun getInstance(context: Context): ChatReadTimeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ChatReadTimeManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * 특정 채팅방의 마지막 읽은 시간을 설정
     */
    fun setLastReadTime(chatId: Int) {
        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit()
            .putLong("${KEY_PREFIX}${chatId}", currentTime)
            .apply()
        Log.d("ChatReadTimeManager", "채팅방 $chatId 마지막 읽은 시간 설정: $currentTime")
    }
    
    /**
     * 특정 채팅방의 마지막 읽은 시간을 가져오기
     */
    fun getLastReadTime(chatId: Int): Long {
        return sharedPreferences.getLong("${KEY_PREFIX}${chatId}", 0L)
    }
    
    /**
     * 특정 채팅방의 읽은 시간 기록 삭제
     */
    fun clearLastReadTime(chatId: Int) {
        sharedPreferences.edit()
            .remove("${KEY_PREFIX}${chatId}")
            .apply()
        Log.d("ChatReadTimeManager", "채팅방 $chatId 읽은 시간 기록 삭제")
    }
    
    /**
     * 모든 채팅방 읽은 시간 기록 삭제
     */
    fun clearAllReadTimes() {
        sharedPreferences.edit().clear().apply()
        Log.d("ChatReadTimeManager", "모든 채팅방 읽은 시간 기록 삭제")
    }
} 
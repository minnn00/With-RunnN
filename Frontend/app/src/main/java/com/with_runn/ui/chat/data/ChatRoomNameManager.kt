package com.with_runn.ui.chat.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * 채팅방 이름을 로컬에 저장하고 관리하는 클래스
 * SharedPreferences를 사용하여 로컬에 저장
 */
class ChatRoomNameManager private constructor(context: Context) {
    
    companion object {
        private const val PREF_NAME = "chat_room_names"
        private const val KEY_PREFIX = "chat_room_name_"
        
        @Volatile
        private var INSTANCE: ChatRoomNameManager? = null
        
        fun getInstance(context: Context): ChatRoomNameManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ChatRoomNameManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * 특정 채팅방의 이름을 저장
     */
    fun saveChatRoomName(chatId: Int, roomName: String) {
        val key = "${KEY_PREFIX}${chatId}"
        sharedPreferences.edit()
            .putString(key, roomName)
            .apply()
        Log.d("ChatRoomNameManager", "채팅방 $chatId 이름 저장: $roomName")
    }
    
    /**
     * 특정 채팅방의 이름을 가져오기
     */
    fun getChatRoomName(chatId: Int): String? {
        val key = "${KEY_PREFIX}${chatId}"
        val roomName = sharedPreferences.getString(key, null)
        Log.d("ChatRoomNameManager", "채팅방 $chatId 이름 로드: $roomName")
        return roomName
    }
    
    /**
     * 특정 채팅방의 이름 기록 삭제
     */
    fun clearChatRoomName(chatId: Int) {
        val key = "${KEY_PREFIX}${chatId}"
        sharedPreferences.edit()
            .remove(key)
            .apply()
        Log.d("ChatRoomNameManager", "채팅방 $chatId 이름 기록 삭제")
    }
    
    /**
     * 모든 채팅방 이름 기록 삭제
     */
    fun clearAllChatRoomNames() {
        sharedPreferences.edit()
            .clear()
            .apply()
        Log.d("ChatRoomNameManager", "모든 채팅방 이름 기록 삭제")
    }
    
    /**
     * 특정 채팅방의 이름이 저장되어 있는지 확인
     */
    fun hasChatRoomName(chatId: Int): Boolean {
        val key = "${KEY_PREFIX}${chatId}"
        return sharedPreferences.contains(key)
    }
} 
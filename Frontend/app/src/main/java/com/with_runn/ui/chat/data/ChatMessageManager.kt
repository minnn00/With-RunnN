package com.with_runn.ui.chat.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.with_runn.ui.chat.model.Message

/**
 * 채팅 메시지를 로컬에 저장하고 로드하는 클래스
 * SharedPreferences를 사용하여 JSON 형태로 저장
 */
class ChatMessageManager private constructor(context: Context) {
    
    companion object {
        private const val PREF_NAME = "chat_messages"
        private const val KEY_PREFIX = "chat_messages_"
        
        @Volatile
        private var INSTANCE: ChatMessageManager? = null
        
        fun getInstance(context: Context): ChatMessageManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ChatMessageManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    /**
     * 특정 채팅방의 메시지들을 저장
     */
    fun saveMessages(chatId: Int, messages: List<Message>) {
        val key = "${KEY_PREFIX}${chatId}"
        val json = gson.toJson(messages)
        sharedPreferences.edit()
            .putString(key, json)
            .apply()
        Log.d("ChatMessageManager", "채팅방 $chatId 메시지 저장: ${messages.size}개")
    }
    
    /**
     * 특정 채팅방의 메시지들을 로드
     */
    fun loadMessages(chatId: Int): List<Message> {
        val key = "${KEY_PREFIX}${chatId}"
        val json = sharedPreferences.getString(key, null)
        
        return if (json != null) {
            try {
                val type = object : TypeToken<List<Message>>() {}.type
                val messages = gson.fromJson<List<Message>>(json, type)
                Log.d("ChatMessageManager", "채팅방 $chatId 메시지 로드: ${messages.size}개")
                messages
            } catch (e: Exception) {
                Log.e("ChatMessageManager", "메시지 로드 실패", e)
                emptyList()
            }
        } else {
            Log.d("ChatMessageManager", "채팅방 $chatId 저장된 메시지 없음")
            emptyList()
        }
    }
    
    /**
     * 특정 채팅방에 새 메시지 추가
     */
    fun addMessage(chatId: Int, message: Message) {
        val currentMessages = loadMessages(chatId).toMutableList()
        currentMessages.add(message)
        saveMessages(chatId, currentMessages)
        Log.d("ChatMessageManager", "채팅방 $chatId 새 메시지 추가: ${message.content}")
    }
    
    /**
     * 특정 채팅방의 메시지들 삭제
     */
    fun clearMessages(chatId: Int) {
        val key = "${KEY_PREFIX}${chatId}"
        sharedPreferences.edit()
            .remove(key)
            .apply()
        Log.d("ChatMessageManager", "채팅방 $chatId 메시지 삭제")
    }
    
    /**
     * 모든 채팅방 메시지 삭제
     */
    fun clearAllMessages() {
        sharedPreferences.edit()
            .clear()
            .apply()
        Log.d("ChatMessageManager", "모든 채팅방 메시지 삭제")
    }
} 
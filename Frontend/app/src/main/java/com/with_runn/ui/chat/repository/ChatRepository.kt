package com.with_runn.ui.chat.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.with_runn.ui.chat.model.ChatRoom
import com.with_runn.ui.chat.model.Message
import com.with_runn.ui.chat.model.dto.ChatRoomDto
import com.with_runn.ui.chat.model.dto.MessageDto
import com.with_runn.ui.chat.model.mapper.ChatRoomMapper.toChatRoom
import com.with_runn.ui.chat.model.mapper.MessageMapper.toMessage
import com.with_runn.ui.chat.network.RetrofitClient

/**
 * 채팅 관련 데이터 처리를 담당하는 Repository
 */
class ChatRepository {
    
    private val chatApiService = RetrofitClient.chatApiService
    
    /**
     * 채팅방 목록을 가져와서 UI 모델로 변환
     */
    fun getChatRooms(callback: (Result<List<ChatRoom>>) -> Unit) {
        chatApiService.getChatList().enqueue(object : retrofit2.Callback<List<ChatRoomDto>> {
            override fun onResponse(
                call: retrofit2.Call<List<ChatRoomDto>>,
                response: retrofit2.Response<List<ChatRoomDto>>
            ) {
                if (response.isSuccessful) {
                    val chatRoomDtos = response.body() ?: emptyList()
                    val chatRooms = chatRoomDtos.map { it.toChatRoom() }
                    callback(Result.success(chatRooms))
                } else {
                    callback(Result.failure(Exception("API 호출 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<List<ChatRoomDto>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 채팅방 메시지 목록을 가져와서 UI 모델로 변환
     */
    fun getChatMessages(chatId: Int, callback: (Result<List<Message>>) -> Unit) {
        chatApiService.getChatMessages(chatId).enqueue(object : retrofit2.Callback<List<MessageDto>> {
            override fun onResponse(
                call: retrofit2.Call<List<MessageDto>>,
                response: retrofit2.Response<List<MessageDto>>
            ) {
                if (response.isSuccessful) {
                    val messageDtos = response.body() ?: emptyList()
                    val messages = messageDtos.map { it.toMessage() }
                    callback(Result.success(messages))
                } else {
                    callback(Result.failure(Exception("메시지 조회 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<List<MessageDto>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
} 
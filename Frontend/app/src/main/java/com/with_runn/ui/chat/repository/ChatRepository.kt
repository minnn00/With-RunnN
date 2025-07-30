package com.with_runn.ui.chat.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.with_runn.ui.chat.model.ChatRoom
import com.with_runn.ui.chat.model.Message
import com.with_runn.ui.chat.model.dto.ChatRoomDto
import com.with_runn.ui.chat.model.dto.MessageDto
import com.with_runn.ui.chat.model.dto.InviteUserRequest
import com.with_runn.ui.chat.model.dto.InviteUserResponse
import com.with_runn.ui.chat.model.dto.UpdateChatNameResponse
import com.with_runn.ui.chat.model.dto.CreateChatRequest
import com.with_runn.ui.chat.model.mapper.ChatRoomMapper.toChatRoom
import com.with_runn.ui.chat.model.mapper.MessageMapper.toMessage
import com.with_runn.ui.chat.network.RetrofitClient
import android.util.Log

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
    
    /**
     * 채팅방에 사용자 초대
     */
    fun inviteUsers(chatId: Int, userIds: List<Int>, callback: (Result<Int>) -> Unit) {
        Log.d("ChatRepository", "초대 요청 시작: chatId=$chatId, userIds=$userIds")
        val request = InviteUserRequest(userIds)
        Log.d("ChatRepository", "요청 객체 생성: $request")
        
        chatApiService.inviteUsers(chatId, request).enqueue(object : retrofit2.Callback<InviteUserResponse> {
            override fun onResponse(
                call: retrofit2.Call<InviteUserResponse>,
                response: retrofit2.Response<InviteUserResponse>
            ) {
                if (response.isSuccessful) {
                    val inviteResponse = response.body()
                    if (inviteResponse != null) {
                        callback(Result.success(inviteResponse.chatId))
                    } else {
                        callback(Result.failure(Exception("응답 데이터가 null입니다")))
                    }
                } else {
                    callback(Result.failure(Exception("초대 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<InviteUserResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 채팅방 삭제/떠나기
     */
    fun deleteChatRoom(chatId: Int, callback: (Result<Unit>) -> Unit) {
        Log.d("ChatRepository", "채팅방 삭제 요청 시작: chatId=$chatId")
        
        chatApiService.deleteChatRoom(chatId).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(
                call: retrofit2.Call<Void>,
                response: retrofit2.Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("ChatRepository", "✅ 채팅방 삭제 성공: chatId=$chatId")
                    callback(Result.success(Unit))
                } else {
                    Log.e("ChatRepository", "❌ 채팅방 삭제 실패: ${response.code()}")
                    callback(Result.failure(Exception("채팅방 삭제 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                Log.e("ChatRepository", "❌ 채팅방 삭제 네트워크 실패", t)
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 채팅방 이름 설정
     */
    fun updateChatName(chatId: Int, newName: String, callback: (Result<UpdateChatNameResponse>) -> Unit) {
        Log.d("ChatRepository", "채팅방 이름 설정 요청 시작: chatId=$chatId, newName=$newName")
        
        chatApiService.updateChatName(chatId, newName).enqueue(object : retrofit2.Callback<UpdateChatNameResponse> {
            override fun onResponse(
                call: retrofit2.Call<UpdateChatNameResponse>,
                response: retrofit2.Response<UpdateChatNameResponse>
            ) {
                if (response.isSuccessful) {
                    val updateResponse = response.body()
                    if (updateResponse != null) {
                        Log.d("ChatRepository", "✅ 채팅방 이름 설정 성공: chatId=${updateResponse.chatId}, name=${updateResponse.name}")
                        callback(Result.success(updateResponse))
                    } else {
                        Log.e("ChatRepository", "❌ 채팅방 이름 설정 실패: 응답 데이터가 null")
                        callback(Result.failure(Exception("응답 데이터가 null입니다")))
                    }
                } else {
                    Log.e("ChatRepository", "❌ 채팅방 이름 설정 실패: ${response.code()}")
                    callback(Result.failure(Exception("채팅방 이름 설정 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<UpdateChatNameResponse>, t: Throwable) {
                Log.e("ChatRepository", "❌ 채팅방 이름 설정 네트워크 실패", t)
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 상대방과 채팅 생성
     */
    fun createChatRoom(userId: Int, targetUserId: Int, callback: (Result<Unit>) -> Unit) {
        Log.d("ChatRepository", "채팅방 생성 요청 시작: userId=$userId, targetUserId=$targetUserId")
        
        val request = CreateChatRequest(userId, targetUserId)
        Log.d("ChatRepository", "요청 객체 생성: $request")
        
        chatApiService.createChatRoom(request).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(
                call: retrofit2.Call<Void>,
                response: retrofit2.Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("ChatRepository", "✅ 채팅방 생성 성공: userId=$userId, targetUserId=$targetUserId")
                    callback(Result.success(Unit))
                } else {
                    Log.e("ChatRepository", "❌ 채팅방 생성 실패: ${response.code()}")
                    callback(Result.failure(Exception("채팅방 생성 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                Log.e("ChatRepository", "❌ 채팅방 생성 네트워크 실패", t)
                callback(Result.failure(t))
            }
        })
    }
} 
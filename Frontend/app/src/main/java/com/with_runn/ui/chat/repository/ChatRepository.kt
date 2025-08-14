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
import com.with_runn.ui.chat.model.dto.InviteUserDto
import com.with_runn.ui.chat.model.mapper.ChatRoomMapper.toChatRoom
import com.with_runn.ui.chat.model.mapper.MessageMapper.toMessage
import com.with_runn.ui.chat.network.RetrofitClient
import android.util.Log
import com.with_runn.ui.chat.model.dto.MessageListResponse
import com.with_runn.ui.chat.model.dto.InviteUser
import com.with_runn.ui.chat.model.dto.ChatListResponse
import com.with_runn.ui.chat.model.dto.InviteListResponse
import com.with_runn.ui.chat.model.dto.CreateChatResponse
import com.with_runn.ui.chat.model.dto.SendMessageRequest
import com.with_runn.ui.chat.model.dto.LeaveChatRoomRequest
import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * 채팅 관련 데이터 처리를 담당하는 Repository
 */
class ChatRepository {
    
    private val chatApiService = RetrofitClient.chatApiService
    
    /**
     * 채팅방 목록을 가져와서 UI 모델로 변환
     */
    fun getChatRooms(callback: (Result<List<ChatRoom>>) -> Unit) {
        chatApiService.getChatList().enqueue(object : retrofit2.Callback<ChatListResponse> {
            override fun onResponse(
                call: retrofit2.Call<ChatListResponse>,
                response: retrofit2.Response<ChatListResponse>
            ) {
                if (response.isSuccessful) {
                    val chatListResponse = response.body()
                    if (chatListResponse != null && chatListResponse.success) {
                        val chatRoomDtos = chatListResponse.result
                        val chatRooms = chatRoomDtos.map { it.toChatRoom() }
                        callback(Result.success(chatRooms))
                    } else {
                        callback(Result.failure(Exception("채팅방 목록 조회 실패: 응답이 성공하지 않음")))
                    }
                } else {
                    // 400 에러인 경우 (참여 중인 채팅방이 없음) 빈 리스트 반환
                    if (response.code() == 400) {
                        Log.d("ChatRepository", "참여 중인 채팅방이 없습니다. 빈 리스트를 반환합니다.")
                        callback(Result.success(emptyList()))
                    } else {
                        callback(Result.failure(Exception("API 호출 실패: ${response.code()}")))
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<ChatListResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 채팅방 메시지 목록을 가져와서 UI 모델로 변환
     */
    fun getChatMessages(chatId: Int, callback: (Result<List<Message>>) -> Unit) {
        Log.d("ChatRepository", "메시지 조회 시작: chatId=$chatId")
        
        chatApiService.getChatMessages(chatId).enqueue(object : retrofit2.Callback<MessageListResponse> {
            override fun onResponse(
                call: retrofit2.Call<MessageListResponse>,
                response: retrofit2.Response<MessageListResponse>
            ) {
                if (response.isSuccessful) {
                    val messageListResponse = response.body()
                    if (messageListResponse != null && messageListResponse.success) {
                        val messageDtos = messageListResponse.result
                        val messages = messageDtos.map { it.toMessage() }
                        Log.d("ChatRepository", "✅ 메시지 조회 성공: ${messages.size}개")
                        callback(Result.success(messages))
                    } else {
                        Log.e("ChatRepository", "❌ 메시지 조회 실패: 응답이 성공하지 않음")
                        callback(Result.failure(Exception("메시지 조회 실패: 응답이 성공하지 않음")))
                    }
                } else {
                    // 500 에러인 경우 (서버 오류) 특별 처리
                    if (response.code() == 500) {
                        val errorBody = response.errorBody()?.string()
                        Log.w("ChatRepository", "⚠️ 서버 오류 발생: $errorBody")
                        
                        // 에러 응답에서 메시지 추출
                        val errorMessage = try {
                            if (!errorBody.isNullOrEmpty()) {
                                val gson = Gson()
                                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                                jsonObject.get("message")?.asString ?: "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                            } else {
                                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                            }
                        } catch (e: Exception) {
                            Log.e("ChatRepository", "에러 응답 파싱 실패", e)
                            "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                        }
                        
                        callback(Result.failure(Exception(errorMessage)))
                    } else {
                        Log.e("ChatRepository", "❌ 메시지 조회 실패: ${response.code()}")
                        callback(Result.failure(Exception("메시지 조회 실패: ${response.code()}")))
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<MessageListResponse>, t: Throwable) {
                Log.e("ChatRepository", "❌ 메시지 조회 네트워크 실패", t)
                
                // EOFException인 경우 특별 처리
                val errorMessage = when {
                    t is java.io.EOFException -> "서버 연결이 끊어졌습니다. 잠시 후 다시 시도해주세요."
                    t is java.net.SocketTimeoutException -> "요청 시간이 초과되었습니다. 네트워크 연결을 확인해주세요."
                    t is java.net.UnknownHostException -> "서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요."
                    else -> "네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                }
                
                callback(Result.failure(Exception(errorMessage)))
            }
        })
    }

    /**
     * 이전 채팅 내역 추가 조회 (cursorId 이전 30개)
     */
    fun getChatMessagesBefore(chatId: Int, cursorMessageId: Int, callback: (Result<List<Message>>) -> Unit) {
        chatApiService.getChatMessagesBefore(chatId, cursorMessageId).enqueue(object : retrofit2.Callback<MessageListResponse> {
            override fun onResponse(
                call: retrofit2.Call<MessageListResponse>,
                response: retrofit2.Response<MessageListResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        val messages = body.result.map { it.toMessage() }
                        callback(Result.success(messages))
                    } else {
                        // CHAT4009 등 에러 코드 대응
                        callback(Result.failure(Exception(body?.message ?: "이전 메시지 조회 실패")))
                    }
                } else {
                    callback(Result.failure(Exception("이전 메시지 조회 실패: ${response.code()}")))
                }
            }
            override fun onFailure(call: retrofit2.Call<MessageListResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 메시지 전송
     */
    fun sendMessage(chatId: Int, message: String, callback: (Result<Unit>) -> Unit) {
        Log.d("ChatRepository", "메시지 전송 시작: chatId=$chatId, message=$message")
        
        val currentUserId = com.with_runn.data.TokenManager.getCurrentUserId()
        val request = SendMessageRequest(
            userId = currentUserId,
            message = message
        )
        
        chatApiService.sendMessage(chatId, request).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(
                call: retrofit2.Call<Void>,
                response: retrofit2.Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("ChatRepository", "✅ 메시지 전송 성공")
                    callback(Result.success(Unit))
                } else {
                    Log.e("ChatRepository", "❌ 메시지 전송 실패: ${response.code()}")
                    callback(Result.failure(Exception("메시지 전송 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                Log.e("ChatRepository", "❌ 메시지 전송 네트워크 실패", t)
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 채팅방에 사용자 초대
     */
    fun inviteUsers(chatId: Int, username: String, inviteUserList: List<InviteUser>, callback: (Result<Int>) -> Unit) {
        Log.d("ChatRepository", "초대 요청 시작: chatId=$chatId, username=$username, inviteUserList=$inviteUserList")
        val request = InviteUserRequest(username, inviteUserList)
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
     * 채팅방 나가기 (새 API 명세서)
     * BE에 사용자가 채팅방을 떠난 시간을 기록
     */
    fun leaveChatRoom(chatId: Int, callback: (Result<Unit>) -> Unit) {
        Log.d("ChatRepository", "채팅방 나가기 요청 시작: chatId=$chatId")
        
        val timestamp = System.currentTimeMillis()
        val request = LeaveChatRoomRequest(timestamp = timestamp)
        
        chatApiService.leaveChatRoom(chatId, request).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(
                call: retrofit2.Call<Void>,
                response: retrofit2.Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("ChatRepository", "✅ 채팅방 나가기 성공: chatId=$chatId, timestamp=$timestamp")
                    callback(Result.success(Unit))
                } else {
                    // 500 에러인 경우 (서버 오류) 특별 처리
                    if (response.code() == 500) {
                        val errorBody = response.errorBody()?.string()
                        Log.w("ChatRepository", "⚠️ 서버 오류 발생: $errorBody")
                        
                        // 에러 응답에서 메시지 추출
                        val errorMessage = try {
                            if (!errorBody.isNullOrEmpty()) {
                                val gson = Gson()
                                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                                jsonObject.get("message")?.asString ?: "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                            } else {
                                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                            }
                        } catch (e: Exception) {
                            Log.e("ChatRepository", "에러 응답 파싱 실패", e)
                            "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                        }
                        
                        callback(Result.failure(Exception(errorMessage)))
                    } else {
                        Log.e("ChatRepository", "❌ 채팅방 나가기 실패: ${response.code()}")
                        callback(Result.failure(Exception("채팅방 나가기 실패: ${response.code()}")))
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                Log.e("ChatRepository", "❌ 채팅방 나가기 네트워크 실패", t)
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
                        Log.d("ChatRepository", "✅ 채팅방 이름 설정 성공: code=${updateResponse.code}, message=${updateResponse.message}")
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
     * 상대방과 채팅 생성 후 생성된 채팅방 찾기
     */
    fun createChatRoomAndFind(userId: Int, targetUserId: Int, friendName: String, callback: (Result<ChatRoom?>) -> Unit) {
        Log.d("ChatRepository", "채팅방 생성 및 찾기 시작: userId=$userId, targetUserId=$targetUserId, friendName=$friendName")
        
        val request = CreateChatRequest(
            userId = userId,
            targetUserId = targetUserId
        )
        Log.d("ChatRepository", "요청 객체 생성: $request")
        
        chatApiService.createChatRoom(targetUserId, request).enqueue(object : retrofit2.Callback<CreateChatResponse> {
            override fun onResponse(
                call: retrofit2.Call<CreateChatResponse>,
                response: retrofit2.Response<CreateChatResponse>
            ) {
                // 500 에러인 경우 (서버 오류) 특별 처리
                if (response.code() == 500) {
                    val errorBody = response.errorBody()?.string()
                    Log.w("ChatRepository", "⚠️ 서버 오류 발생: $errorBody")
                    
                    // 에러 응답에서 메시지 추출
                    val errorMessage = try {
                        if (!errorBody.isNullOrEmpty()) {
                            val gson = Gson()
                            val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                            jsonObject.get("message")?.asString ?: "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                        } else {
                            "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                        }
                    } catch (e: Exception) {
                        Log.e("ChatRepository", "에러 응답 파싱 실패", e)
                        "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    }
                    
                    Log.d("ChatRepository", "500 에러 처리 완료: $errorMessage")
                    callback(Result.failure(Exception(errorMessage)))
                    return
                }
                
                if (response.isSuccessful) {
                    val createResponse = response.body()
                    if (createResponse != null && createResponse.success) {
                        Log.d("ChatRepository", "✅ 채팅방 생성 성공: code=${createResponse.code}, message=${createResponse.message}")
                        
                        // 생성 후 잠시 대기 후 채팅 목록 조회
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            findCreatedChatRoom(userId, friendName, callback)
                        }, 1000) // 1초 대기
                    } else {
                        Log.e("ChatRepository", "❌ 채팅방 생성 실패: 응답이 성공하지 않음")
                        callback(Result.failure(Exception("채팅방 생성 실패: 응답이 성공하지 않음")))
                    }
                } else {
                    Log.e("ChatRepository", "❌ 채팅방 생성 실패: ${response.code()}")
                    callback(Result.failure(Exception("채팅방 생성 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<CreateChatResponse>, t: Throwable) {
                Log.e("ChatRepository", "❌ 채팅방 생성 네트워크 실패", t)
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 생성된 채팅방 찾기
     */
    private fun findCreatedChatRoom(userId: Int, friendName: String, callback: (Result<ChatRoom?>) -> Unit) {
        Log.d("ChatRepository", "생성된 채팅방 찾기 시작: friendName=$friendName")
        
        chatApiService.getChatList().enqueue(object : retrofit2.Callback<ChatListResponse> {
            override fun onResponse(
                call: retrofit2.Call<ChatListResponse>,
                response: retrofit2.Response<ChatListResponse>
            ) {
                if (response.isSuccessful) {
                    val chatListResponse = response.body()
                    if (chatListResponse != null && chatListResponse.success) {
                        val chatRoomDtos = chatListResponse.result
                        val chatRooms = chatRoomDtos.map { it.toChatRoom() }
                        
                        // 가장 최근에 생성된 채팅방 찾기 (가장 큰 chatId)
                        val createdChatRoom = chatRooms.maxByOrNull { it.chatId }
                        
                        if (createdChatRoom != null) {
                            Log.d("ChatRepository", "✅ 최근 생성된 채팅방 찾음: chatId=${createdChatRoom.chatId}, name=${createdChatRoom.name}")
                            callback(Result.success(createdChatRoom))
                        } else {
                            Log.d("ChatRepository", "⚠️ 생성된 채팅방을 찾을 수 없음")
                            callback(Result.success(null))
                        }
                    } else {
                        Log.e("ChatRepository", "❌ 채팅 목록 조회 실패: 응답이 성공하지 않음")
                        callback(Result.failure(Exception("채팅 목록 조회 실패: 응답이 성공하지 않음")))
                    }
                } else {
                    Log.e("ChatRepository", "❌ 채팅 목록 조회 실패: ${response.code()}")
                    callback(Result.failure(Exception("채팅 목록 조회 실패: ${response.code()}")))
                }
            }
            
            override fun onFailure(call: retrofit2.Call<ChatListResponse>, t: Throwable) {
                Log.e("ChatRepository", "❌ 채팅 목록 조회 네트워크 실패", t)
                callback(Result.failure(t))
            }
        })
    }
    
    /**
     * 채팅 초대 목록 조회
     */
    fun getInviteUserList(chatId: Int, callback: (Result<List<InviteUserDto>>) -> Unit) {
        Log.d("ChatRepository", "채팅 초대 목록 조회 시작: chatId=$chatId")
        
        chatApiService.getInviteUserList(chatId).enqueue(object : retrofit2.Callback<InviteListResponse> {
            override fun onResponse(
                call: retrofit2.Call<InviteListResponse>,
                response: retrofit2.Response<InviteListResponse>
            ) {
                Log.d("ChatRepository", "채팅 초대 목록 조회 응답: code=${response.code()}, isSuccessful=${response.isSuccessful()}")
                
                if (response.isSuccessful) {
                    val inviteListResponse = response.body()
                    if (inviteListResponse != null && inviteListResponse.success) {
                        val inviteUserList = inviteListResponse.result
                        Log.d("ChatRepository", "✅ 채팅 초대 목록 조회 성공: ${inviteUserList.size}명")
                        callback(Result.success(inviteUserList))
                    } else {
                        Log.e("ChatRepository", "❌ 채팅 초대 목록 조회 실패: 응답이 성공하지 않음")
                        callback(Result.failure(Exception("채팅 초대 목록 조회 실패: 응답이 성공하지 않음")))
                    }
                } else {
                    // 400 에러인 경우 (채팅방이 꽉 찬 경우) 특별 처리
                    if (response.code() == 400) {
                        val errorBody = response.errorBody()?.string()
                        Log.w("ChatRepository", "⚠️ 채팅방이 꽉 찼습니다: $errorBody")
                        
                        // 에러 응답에서 메시지 추출
                        val errorMessage = try {
                            if (!errorBody.isNullOrEmpty()) {
                                val gson = Gson()
                                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                                jsonObject.get("message")?.asString ?: "채팅방이 꽉 찼습니다. 더 이상 초대할 수 없습니다."
                            } else {
                                "채팅방이 꽉 찼습니다. 더 이상 초대할 수 없습니다."
                            }
                        } catch (e: Exception) {
                            Log.e("ChatRepository", "에러 응답 파싱 실패", e)
                            "채팅방이 꽉 찼습니다. 더 이상 초대할 수 없습니다."
                        }
                        
                        Log.d("ChatRepository", "400 에러 처리 완료: $errorMessage")
                        callback(Result.failure(Exception(errorMessage)))
                    } else {
                        Log.e("ChatRepository", "❌ 채팅 초대 목록 조회 실패: ${response.code()}")
                        callback(Result.failure(Exception("채팅 초대 목록 조회 실패: ${response.code()}")))
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<InviteListResponse>, t: Throwable) {
                Log.e("ChatRepository", "❌ 채팅 초대 목록 조회 네트워크 실패", t)
                callback(Result.failure(t))
            }
        })
    }
} 
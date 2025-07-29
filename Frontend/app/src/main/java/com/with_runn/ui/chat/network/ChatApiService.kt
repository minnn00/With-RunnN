package com.with_runn.ui.chat.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.Query
import com.with_runn.ui.chat.model.dto.ChatRoomDto
import com.with_runn.ui.chat.model.dto.MessageDto
import com.with_runn.ui.chat.model.dto.InviteUserRequest
import com.with_runn.ui.chat.model.dto.InviteUserResponse
import com.with_runn.ui.chat.model.dto.UpdateChatNameResponse
import com.with_runn.ui.chat.model.dto.CreateChatRequest

/**
 * 채팅 관련 API 서비스 인터페이스
 */
interface ChatApiService {
    
    /**
     * 채팅방 목록 조회
     * GET /api/chat/list
     */
    @GET("api/chat/list")
    fun getChatList(): retrofit2.Call<List<ChatRoomDto>>
    
    /**
     * 채팅방 메시지 목록 조회
     * GET /api/chat/{chatId}
     */
    @GET("api/chat/{chatId}")
    fun getChatMessages(@retrofit2.http.Path("chatId") chatId: Int): retrofit2.Call<List<MessageDto>>
    
    /**
     * 채팅방 초대
     * POST /api/chat/{chatId}/invite
     */
    @POST("api/chat/{chatId}/invite")
    fun inviteUsers(
        @retrofit2.http.Path("chatId") chatId: Int,
        @Body request: InviteUserRequest
    ): retrofit2.Call<InviteUserResponse>
    
    /**
     * 채팅방 삭제/떠나기
     * DELETE /api/chat/{chatId}
     */
    @DELETE("api/chat/{chatId}")
    fun deleteChatRoom(@retrofit2.http.Path("chatId") chatId: Int): retrofit2.Call<Void>
    
    /**
     * 채팅방 이름 설정
     * PATCH /api/chat/{chatId}?name={name}
     */
    @PATCH("api/chat/{chatId}")
    fun updateChatName(
        @retrofit2.http.Path("chatId") chatId: Int,
        @Query("name") name: String
    ): retrofit2.Call<UpdateChatNameResponse>
    
    /**
     * 상대방과 채팅 생성
     * POST /api/chat/hello
     */
    @POST("api/chat/hello")
    fun createChatRoom(@Body request: CreateChatRequest): retrofit2.Call<Void>
} 
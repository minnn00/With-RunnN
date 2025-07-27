package com.with_runn.ui.chat.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.with_runn.ui.chat.model.dto.ChatRoomDto
import com.with_runn.ui.chat.model.dto.MessageDto

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
} 
package com.with_runn.share

import retrofit2.http.GET
import retrofit2.http.Header

interface ChatApi {
    @GET("api/chat")
    suspend fun getMyChatRooms(
        @Header("Authorization") bearerToken: String
    ): ChatRoomsEnvelope
}
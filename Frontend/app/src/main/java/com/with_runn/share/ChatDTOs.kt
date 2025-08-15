package com.with_runn.share

import com.google.gson.annotations.SerializedName

data class ChatRoomsEnvelope(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("result") val result: List<ChatRoomDto>?,
    @SerializedName("success") val success: Boolean?
)

data class ChatRoomDto(
    @SerializedName("chatId") val chatId: Int,
    @SerializedName("chatName") val chatName: String?,
    @SerializedName("unReadMsgCount") val unReadMsgCount: Int?,
    @SerializedName("lastReceivedMsg") val lastReceivedMsg: String?,
    @SerializedName("participants") val participants: Int?,
    @SerializedName("usernameList") val usernameList: List<String>?,
    @SerializedName("userProfileList") val userProfileList: List<String>?
)
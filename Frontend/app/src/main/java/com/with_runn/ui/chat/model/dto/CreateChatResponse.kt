package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

data class CreateChatResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("success") val success: Boolean
)

data class CreateChatResult(
    @SerializedName("chatId") val chatId: Int,
    @SerializedName("chatName") val chatName: String? = null
) 
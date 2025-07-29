package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName
 
data class InviteUserResponse(
    @SerializedName("chatId")
    val chatId: Int
) 
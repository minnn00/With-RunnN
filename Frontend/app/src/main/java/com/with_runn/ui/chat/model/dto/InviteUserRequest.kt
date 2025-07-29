package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName
 
data class InviteUserRequest(
    @SerializedName("userId")
    val userId: List<Int>
) 
package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * 채팅방 초대 요청
 */
data class InviteUserRequest(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("inviteUserList")
    val inviteUserList: List<InviteUser>
) 
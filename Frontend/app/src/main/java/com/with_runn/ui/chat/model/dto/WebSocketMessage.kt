package com.with_runn.ui.chat.model.dto

import com.google.gson.annotations.SerializedName

/**
 * WebSocket 메시지 전송 DTO
 */
data class SendMessageRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("message") val message: String,
    @SerializedName("isCourse") val isCourse: Boolean = false
)

/**
 * WebSocket 메시지 수신 DTO (일반 메시지)
 */
data class ReceiveMessageResponse(
    @SerializedName("userId") val userId: Int,
    @SerializedName("userName") val userName: String,
    @SerializedName("userProfileImage") val userProfileImage: String,
    @SerializedName("msg") val msg: String,
    @SerializedName("isCourse") val isCourse: Boolean,
    @SerializedName("createdAt") val createdAt: String
)

/**
 * WebSocket 메시지 수신 DTO (공유 메시지)
 */
data class ReceiveCourseMessageResponse(
    @SerializedName("userId") val userId: Int,
    @SerializedName("userName") val userName: String,
    @SerializedName("userProfileImage") val userProfileImage: String,
    @SerializedName("msg") val msg: String,
    @SerializedName("isCourse") val isCourse: Boolean,
    @SerializedName("courseId") val courseId: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("courseImage") val courseImage: String,
    @SerializedName("courseTag") val courseTag: List<String>
) 
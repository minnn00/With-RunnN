package com.with_runn.ui.chat.network

import android.util.Log
import com.google.gson.Gson
import com.with_runn.ui.chat.model.dto.SendMessageRequest
import com.with_runn.ui.chat.model.dto.ReceiveMessageResponse
import com.with_runn.ui.chat.model.dto.ReceiveCourseMessageResponse
import com.with_runn.ui.chat.model.Message
import com.with_runn.data.TokenManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import java.util.concurrent.atomic.AtomicBoolean

/**
 * STOMP WebSocket 연결 및 메시지 관리를 담당하는 클래스
 */
class WebSocketManager {
    
    companion object {
        private const val TAG = "WebSocketManager"
        private const val BASE_URL = "ws://13.209.75.209:8080/api/ws"
        
        @Volatile
        private var instance: WebSocketManager? = null
        
        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }
    }
    
    private var stompClient: StompClient? = null
    private val isConnected = AtomicBoolean(false)
    private val gson = Gson()
    private val compositeDisposable = CompositeDisposable()
    private var messageListener: ((Message) -> Unit)? = null
    private var currentChatId: Int? = null
    
    /**
     * STOMP WebSocket 연결
     */
    fun connect(onConnected: () -> Unit = {}, onError: (String) -> Unit = {}) {
        if (isConnected.get()) {
            Log.d(TAG, "이미 연결되어 있습니다")
            onConnected()
            return
        }
        
        try {
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, BASE_URL)
            
            compositeDisposable.add(
                stompClient!!.lifecycle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { lifecycleEvent ->
                            when (lifecycleEvent.type) {
                                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED -> {
                                    Log.d(TAG, "✅ STOMP WebSocket 연결 성공")
                                    isConnected.set(true)
                                    onConnected()
                                }
                                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.ERROR -> {
                                    Log.e(TAG, "❌ STOMP WebSocket 연결 오류: ${lifecycleEvent.exception}")
                                    isConnected.set(false)
                                    onError("STOMP WebSocket 연결 오류: ${lifecycleEvent.exception?.message}")
                                }
                                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.CLOSED -> {
                                    Log.d(TAG, "STOMP WebSocket 연결 해제")
                                    isConnected.set(false)
                                }
                                else -> {
                                    Log.d(TAG, "STOMP WebSocket 이벤트: ${lifecycleEvent.type}")
                                }
                            }
                        },
                        { throwable ->
                            Log.e(TAG, "STOMP WebSocket 연결 실패", throwable)
                            isConnected.set(false)
                            onError("STOMP WebSocket 연결 실패: ${throwable.message}")
                        }
                    )
            )
            
            stompClient!!.connect()
            
        } catch (e: Exception) {
            Log.e(TAG, "STOMP 클라이언트 생성 실패", e)
            onError("STOMP 클라이언트 생성 실패: ${e.message}")
        }
    }
    
    /**
     * 채팅방 구독
     */
    fun subscribeToChatRoom(chatId: Int, listener: (Message) -> Unit) {
        if (!isConnected.get()) {
            Log.e(TAG, "WebSocket이 연결되지 않았습니다")
            return
        }
        
        currentChatId = chatId
        messageListener = listener
        
        // STOMP 구독 URL: /sub/{chatId}/msg (API 명세서에 따름)
        val topic = "/sub/$chatId/msg"
        Log.d(TAG, "채팅방 구독 시작: $topic")
        
        compositeDisposable.add(
            stompClient!!.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { topicMessage ->
                        Log.d(TAG, "메시지 수신: ${topicMessage.payload}")
                        handleReceivedMessage(topicMessage.payload)
                    },
                    { throwable ->
                        Log.e(TAG, "채팅방 구독 실패", throwable)
                    }
                )
        )
    }
    
    /**
     * 메시지 전송
     */
    fun sendMessage(chatId: Int, message: String) {
        if (!isConnected.get()) {
            Log.e(TAG, "WebSocket이 연결되지 않았습니다")
            return
        }
        
        try {
            val currentUserId = TokenManager.getCurrentUserId()
            val sendMessageRequest = SendMessageRequest(
                userId = currentUserId,
                message = message
            )
            
            val jsonMessage = gson.toJson(sendMessageRequest)
            // STOMP 발행 URL: /pub/{chatId}/msg (API 명세서에 따름)
            val destination = "/pub/$chatId/msg"
            
            Log.d(TAG, "메시지 전송: $jsonMessage to $destination")
            
            compositeDisposable.add(
                stompClient!!.send(destination, jsonMessage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.d(TAG, "메시지 전송 성공")
                        },
                        { throwable ->
                            Log.e(TAG, "메시지 전송 실패", throwable)
                        }
                    )
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "메시지 전송 실패", e)
        }
    }
    
    /**
     * 수신된 메시지 처리
     */
    private fun handleReceivedMessage(messagePayload: String) {
        try {
            // 일반 메시지인지 공유 메시지인지 확인
            val messageResponse = gson.fromJson(messagePayload, ReceiveMessageResponse::class.java)
            
            if (messageResponse.isCourse) {
                // 공유 메시지인 경우
                val courseMessageResponse = gson.fromJson(messagePayload, ReceiveCourseMessageResponse::class.java)
                val message = Message(
                    messageId = System.currentTimeMillis().toInt(),
                    sender = courseMessageResponse.userName,
                    content = courseMessageResponse.msg,
                    timestamp = formatTimestamp(courseMessageResponse.createdAt),
                    isFromMe = courseMessageResponse.userId == TokenManager.getCurrentUserId(),
                    isCourseShare = true,
                    courseId = courseMessageResponse.courseId,
                    courseImage = courseMessageResponse.courseImage,
                    courseTags = courseMessageResponse.courseTag
                )
                messageListener?.invoke(message)
            } else {
                // 일반 메시지인 경우
                val message = Message(
                    messageId = System.currentTimeMillis().toInt(),
                    sender = messageResponse.userName,
                    content = messageResponse.msg,
                    timestamp = formatTimestamp(messageResponse.createdAt),
                    isFromMe = messageResponse.userId == TokenManager.getCurrentUserId(),
                    isSystemMessage = false,
                    isCourseShare = false
                )
                messageListener?.invoke(message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "메시지 파싱 실패", e)
        }
    }
    
    /**
     * 연결 해제
     */
    fun disconnect() {
        try {
            compositeDisposable.clear()
            stompClient?.disconnect()
            isConnected.set(false)
            messageListener = null
            currentChatId = null
            Log.d(TAG, "STOMP WebSocket 연결 해제 완료")
        } catch (e: Exception) {
            Log.e(TAG, "STOMP WebSocket 연결 해제 실패", e)
        }
    }
    
    /**
     * 연결 상태 확인
     */
    fun isConnected(): Boolean {
        return isConnected.get()
    }
    
    /**
     * 타임스탬프 포맷팅
     */
    private fun formatTimestamp(timestamp: String): String {
        return try {
            // "2025-07-16T15:32:10" -> "오후 3:32" 형태로 변환
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("a h:mm", java.util.Locale.KOREAN)
            val date = inputFormat.parse(timestamp)
            outputFormat.format(date ?: java.util.Date())
        } catch (e: Exception) {
            "방금 전"
        }
    }
} 
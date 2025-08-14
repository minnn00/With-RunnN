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
    private val topicSubscriptions = mutableMapOf<Int, io.reactivex.disposables.Disposable>()
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
        
        // 이미 구독 중이면 중복 구독 방지 (리스너만 최신으로 교체됨)
        if (topicSubscriptions.containsKey(chatId)) {
            Log.d(TAG, "이미 구독 중: $topic (리스너만 갱신)")
            return
        }

        val disposable = stompClient!!.topic(topic)
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

        topicSubscriptions[chatId] = disposable
        compositeDisposable.add(disposable)
    }

    fun unsubscribeFromChatRoom(chatId: Int) {
        topicSubscriptions.remove(chatId)?.dispose()
        Log.d(TAG, "채팅방 구독 해제: /sub/$chatId/msg")
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
        // 일부 서버는 문자열 메시지를 그대로 보내기도 함 (예: "현재사용자님이 X님을 초대하였습니다.")
        var payload = messagePayload.trim()
        if (payload.startsWith("\"") && payload.endsWith("\"")) {
            payload = payload.substring(1, payload.length - 1)
        }

        // 문자열 시스템 메시지 감지: JSON이 아니거나 특정 문구 포함 시
        val looksLikeJson = payload.startsWith("{")
        val isSystemText = payload.contains("초대하였습니다") ||
                payload.contains("채팅방이 생성되었습니다") ||
                payload.contains("상대방과 나누는 첫 대화입니다")

        if (!looksLikeJson || isSystemText) {
            val sanitized = payload.replace("null님", "알 수 없는 사용자님")
            val systemMessage = Message(
                messageId = System.currentTimeMillis().toInt(),
                sender = "시스템",
                content = sanitized,
                timestamp = java.text.SimpleDateFormat("a h:mm", java.util.Locale.KOREAN).format(java.util.Date()),
                isFromMe = false,
                isSystemMessage = true,
                isCourseShare = false
            )
            messageListener?.invoke(systemMessage)
            return
        }

        try {
            // 일반 메시지인지 공유 메시지인지 확인
            val messageResponse = gson.fromJson(payload, ReceiveMessageResponse::class.java)
            if (messageResponse.isCourse) {
                val courseMessageResponse = gson.fromJson(payload, ReceiveCourseMessageResponse::class.java)
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
                val content = (messageResponse.msg ?: "").trim()
                val isSystem = content.contains("초대하였습니다") ||
                        content.contains("채팅방이 생성되었습니다") ||
                        content.contains("상대방과 나누는 첫 대화입니다")
                val message = Message(
                    messageId = System.currentTimeMillis().toInt(),
                    sender = if (isSystem) "시스템" else messageResponse.userName,
                    content = content,
                    timestamp = formatTimestamp(messageResponse.createdAt),
                    isFromMe = if (isSystem) false else messageResponse.userId == TokenManager.getCurrentUserId(),
                    isSystemMessage = isSystem,
                    isCourseShare = false
                )
                messageListener?.invoke(message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "메시지 파싱 실패", e)
            // 폴백: 전체 payload를 시스템 메시지로 표시
            val systemMessage = Message(
                messageId = System.currentTimeMillis().toInt(),
                sender = "시스템",
                content = payload,
                timestamp = java.text.SimpleDateFormat("a h:mm", java.util.Locale.KOREAN).format(java.util.Date()),
                isFromMe = false,
                isSystemMessage = true,
                isCourseShare = false
            )
            messageListener?.invoke(systemMessage)
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
            topicSubscriptions.clear()
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
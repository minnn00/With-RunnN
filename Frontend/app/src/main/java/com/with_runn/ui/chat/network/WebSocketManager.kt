package com.with_runn.ui.chat.network

import android.util.Log
import com.google.gson.Gson
import com.with_runn.ui.chat.model.dto.SendMessageRequest
import com.with_runn.ui.chat.model.dto.ReceiveMessageResponse
import com.with_runn.ui.chat.model.dto.ReceiveCourseMessageResponse
import com.with_runn.ui.chat.model.Message
import java.util.concurrent.atomic.AtomicBoolean
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.java_websocket.framing.Framedata
import java.net.URI

/**
 * WebSocket 연결 및 메시지 관리를 담당하는 클래스
 */
class WebSocketManager {
    
    companion object {
        private const val TAG = "WebSocketManager"
        private const val BASE_URL = "ws://13.209.75.209:8080/api/ws" // 실제 서버 주소로 변경 필요
        
        @Volatile
        private var instance: WebSocketManager? = null
        
        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }
    }
    
    private var webSocketClient: ChatWebSocketClient? = null
    private val isConnected = AtomicBoolean(false)
    private val gson = Gson()
    private var messageListener: ((Message) -> Unit)? = null
    
    /**
     * WebSocket 연결
     */
    fun connect(onConnected: () -> Unit, onError: (String) -> Unit) {
        if (isConnected.get()) {
            Log.d(TAG, "이미 연결되어 있습니다")
            onConnected()
            return
        }
        
        try {
            val uri = URI(BASE_URL)
            webSocketClient = ChatWebSocketClient(uri, onConnected, onError)
            webSocketClient?.connect()
        } catch (e: Exception) {
            Log.e(TAG, "WebSocket 클라이언트 생성 실패", e)
            onError("WebSocket 클라이언트 생성 실패: ${e.message}")
        }
    }
    
    /**
     * 채팅방 구독 (일반 WebSocket 메시지로 처리)
     */
    fun subscribeToChatRoom(chatId: Int, listener: (Message) -> Unit) {
        if (!isConnected.get()) {
            Log.e(TAG, "WebSocket이 연결되지 않았습니다")
            return
        }
        
        messageListener = listener
        Log.d(TAG, "채팅방 구독 시작: chatId=$chatId")
    }
    
    /**
     * 메시지 전송
     */
    fun sendMessage(chatId: Int, message: String, isCourse: Boolean = false) {
        if (!isConnected.get()) {
            Log.e(TAG, "WebSocket이 연결되지 않았습니다")
            return
        }
        
        try {
            val sendMessageRequest = SendMessageRequest(
                userId = 10, // 현재 사용자 ID
                message = message,
                isCourse = isCourse
            )
            
            val jsonMessage = gson.toJson(sendMessageRequest)
            
            Log.d(TAG, "메시지 전송: $jsonMessage")
            webSocketClient?.send(jsonMessage)
            
        } catch (e: Exception) {
            Log.e(TAG, "메시지 전송 실패", e)
        }
    }
    
    /**
     * 연결 해제
     */
    fun disconnect() {
        try {
            webSocketClient?.close()
            isConnected.set(false)
            messageListener = null
            Log.d(TAG, "WebSocket 연결 해제 완료")
        } catch (e: Exception) {
            Log.e(TAG, "WebSocket 연결 해제 실패", e)
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
        // "2025-07-16T15:32:10" -> "오후 3:32" 형태로 변환
        return try {
            // 간단한 포맷팅 (실제로는 더 정교한 시간 처리 필요)
            "방금 전"
        } catch (e: Exception) {
            "방금 전"
        }
    }
    
    /**
     * WebSocket 클라이언트 구현
     */
    private inner class ChatWebSocketClient(
        serverUri: URI,
        private val onConnected: () -> Unit,
        private val onError: (String) -> Unit
    ) : WebSocketClient(serverUri) {
        
        override fun onOpen(handshakedata: ServerHandshake?) {
            Log.d(TAG, "✅ WebSocket 연결 성공")
            isConnected.set(true)
            onConnected()
        }
        
        override fun onMessage(message: String?) {
            Log.d(TAG, "메시지 수신: $message")
            message?.let { msg ->
                try {
                    // 일반 메시지인지 공유 메시지인지 확인
                    val messageResponse = gson.fromJson(msg, ReceiveMessageResponse::class.java)
                    
                    if (messageResponse.isCourse) {
                        // 공유 메시지인 경우
                        val courseMessageResponse = gson.fromJson(msg, ReceiveCourseMessageResponse::class.java)
                        val message = Message(
                            messageId = System.currentTimeMillis().toInt(),
                            sender = courseMessageResponse.userName,
                            content = courseMessageResponse.msg,
                            timestamp = formatTimestamp(courseMessageResponse.createdAt),
                            isFromMe = courseMessageResponse.userId == 10, // 현재 사용자 ID
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
                            isFromMe = messageResponse.userId == 10 // 현재 사용자 ID
                        )
                        messageListener?.invoke(message)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "메시지 파싱 실패", e)
                }
            }
        }
        
        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            Log.d(TAG, "WebSocket 연결 해제: code=$code, reason=$reason")
            isConnected.set(false)
        }
        
        override fun onError(ex: Exception?) {
            Log.e(TAG, "❌ WebSocket 연결 실패", ex)
            isConnected.set(false)
            onError("WebSocket 연결 실패: ${ex?.message}")
        }
    }
} 
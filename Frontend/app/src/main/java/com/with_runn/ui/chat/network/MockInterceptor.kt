package com.with_runn.ui.chat.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * 테스트용 Mock Interceptor
 * 실제 서버 없이도 API 응답을 시뮬레이션
 */
class MockInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        
        return when {
            url.contains("api/chat/list") -> {
                // Mock 응답 데이터
                val mockResponse = """
                    [
                        {
                            "chatId": 1,
                            "users": ["조니"],
                            "userProfiles": ["profile1"],
                            "participants": 1,
                            "lastMsgReceived": "2025-07-07",
                            "lastMessage": "네 좋아요~^^",
                            "notificationCount": 2
                        },
                        {
                            "chatId": 2,
                            "users": ["마루"],
                            "userProfiles": ["profile2"],
                            "participants": 1,
                            "lastMsgReceived": "2025-07-06",
                            "lastMessage": "오늘 산책하실래요?",
                            "notificationCount": 0
                        },
                        {
                            "chatId": 3,
                            "users": ["이름 없는 사용자"],
                            "userProfiles": ["profile3"],
                            "participants": 1,
                            "lastMsgReceived": "2025-05-29",
                            "lastMessage": "안녕하세요!",
                            "notificationCount": 1
                        },
                        {
                            "chatId": 4,
                            "users": ["초코", "모찌"],
                            "userProfiles": ["profile4", "profile5"],
                            "participants": 2,
                            "lastMsgReceived": "2025-05-12",
                            "lastMessage": "같이 산책가요!",
                            "notificationCount": 3
                        },
                        {
                            "chatId": 5,
                            "users": ["마루"],
                            "userProfiles": ["profile2"],
                            "participants": 1,
                            "lastMsgReceived": "2025-05-09",
                            "lastMessage": "좋은 하루 되세요",
                            "notificationCount": 0
                        }
                    ]
                """.trimIndent()
                
                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(mockResponse.toResponseBody("application/json".toMediaType()))
                    .build()
            }
            url.contains("api/chat/1") -> {
                // 조니와의 채팅방 메시지 Mock 데이터
                val mockResponse = """
                    [
                        {
                            "messageId": 1,
                            "sender": "조니",
                            "content": "안녕하세요! 추천 친구 보고 연락드려요~",
                            "timestamp": "2025-07-07T10:30:00",
                            "messageType": "TEXT"
                        },
                        {
                            "messageId": 2,
                            "sender": "나",
                            "content": "안녕하세요~!^^",
                            "timestamp": "2025-07-07T10:32:00",
                            "messageType": "TEXT"
                        },
                        {
                            "messageId": 3,
                            "sender": "조니",
                            "content": "혹시 오늘 산책 계획 있으신가요?",
                            "timestamp": "2025-07-07T10:35:00",
                            "messageType": "TEXT"
                        },
                        {
                            "messageId": 4,
                            "sender": "나",
                            "content": "네! 같이 산책하실래요?!",
                            "timestamp": "2025-07-07T10:37:00",
                            "messageType": "TEXT"
                        },
                        {
                            "messageId": 5,
                            "sender": "조니",
                            "content": "좋아요! 그럼 6시에 연남에서 보시는 거 어떠세요?",
                            "timestamp": "2025-07-07T10:40:00",
                            "messageType": "TEXT"
                        },
                        {
                            "messageId": 6,
                            "sender": "나",
                            "content": "좋습니다~!",
                            "timestamp": "2025-07-07T10:42:00",
                            "messageType": "TEXT"
                        },
                        {
                            "messageId": 7,
                            "sender": "시스템",
                            "content": "course_share",
                            "timestamp": "2025-07-07T10:45:00",
                            "messageType": "COURSE_SHARE"
                        }
                    ]
                """.trimIndent()
                
                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(mockResponse.toResponseBody("application/json".toMediaType()))
                    .build()
            }
            else -> {
                // 다른 URL은 실제 네트워크 호출
                chain.proceed(request)
            }
        }
    }
} 
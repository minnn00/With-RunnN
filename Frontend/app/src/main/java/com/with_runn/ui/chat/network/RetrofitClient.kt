package com.with_runn.ui.chat.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 클라이언트 싱글톤 객체
 * API 서비스 인스턴스를 생성하고 관리
 */
object RetrofitClient {
    
    // 임시 베이스 URL (실제 서버 URL로 변경 필요)
    private const val BASE_URL = "https://api.withrunn.com/"
    
    // OkHttpClient 설정
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(MockInterceptor()) // Mock 데이터 사용
        .addInterceptor(createLoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Retrofit 인스턴스
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    // ChatApiService 인스턴스
    val chatApiService: ChatApiService = retrofit.create(ChatApiService::class.java)
    
    /**
     * HTTP 로깅 인터셉터 생성
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
} 
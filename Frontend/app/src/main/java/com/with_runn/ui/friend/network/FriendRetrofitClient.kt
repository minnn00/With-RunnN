package com.with_runn.ui.friend.network

import com.with_runn.data.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object FriendRetrofitClient {
    private const val BASE_URL = "http://13.209.75.209:8080/"

    private val authInterceptor = Interceptor { chain ->
        val token = TokenManager.getAccessToken()
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        chain.proceed(newRequest)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    val friendApiService: FriendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create()) // 텍스트 응답을 위한 컨버터
            .addConverterFactory(GsonConverterFactory.create()) // JSON 응답을 위한 컨버터
            .build()
            .create(FriendApiService::class.java)
    }
} 
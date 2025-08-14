package com.with_runn.data.remote

import com.with_runn.data.api.CourseApi
import com.with_runn.data.api.MyPageApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


object RetrofitInstance {

    private const val BASE_URL = "http://13.209.75.209:8080/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val courseApi: CourseApi by lazy {
        retrofit.create(CourseApi::class.java)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS  // URL/헤더 확인용
        redactHeader("Authorization")                  // 토큰은 로그에 안 찍히게
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val myPageApi: MyPageApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyPageApiService::class.java)

    }
}
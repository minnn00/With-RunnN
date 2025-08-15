package com.with_runn.share

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChatService {
    private const val BASE_URL = "http://13.209.75.209:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ChatApi by lazy {
        retrofit.create(ChatApi::class.java)
    }
}
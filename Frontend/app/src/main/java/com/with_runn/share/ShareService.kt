package com.with_runn.share

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ShareService {
    private const val BASE_URL = "http://13.209.75.209:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ShareApi by lazy { retrofit.create(ShareApi::class.java) }
}
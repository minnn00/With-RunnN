package com.with_runn.mapData

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapSearchService {
    private const val BASE_URL = "http://13.209.75.209:8080/"

    private val logger = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }
    private val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    val api: MapSearchApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapSearchApi::class.java)
    }
}
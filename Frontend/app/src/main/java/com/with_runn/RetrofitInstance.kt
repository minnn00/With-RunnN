package com.with_runn.data.api

import com.with_runn.CourseApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://your.api.base.url/" // TODO: 실제 API 주소로 교체

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON → DTO로 자동 파싱
            .build()
    }

    val api: CourseApi by lazy {
        retrofit.create(CourseApi::class.java)
    }
}

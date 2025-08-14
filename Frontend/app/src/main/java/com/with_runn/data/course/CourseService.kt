package com.with_runn.data.course

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CourseService {
    private const val BASE_URL = "http://13.209.75.209:8080/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api : CourseApi = retrofit.create(CourseApi::class.java)
}
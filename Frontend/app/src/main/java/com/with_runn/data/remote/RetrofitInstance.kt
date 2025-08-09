package com.with_runn.data.remote

import com.with_runn.data.api.CourseApi
import com.with_runn.data.api.MyPageApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    val myPageApi: MyPageApiService by lazy {
        retrofit.create(MyPageApiService::class.java)
    }
}
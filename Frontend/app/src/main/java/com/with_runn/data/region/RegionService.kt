package com.with_runn.data.region

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RegionService {
    private const val BASE_URL = "http://13.209.75.209:8080/"

    val api: RegionApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RegionApi::class.java)
    }
}
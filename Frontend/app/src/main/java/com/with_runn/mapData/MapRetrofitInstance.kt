package com.with_runn.mapData

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapRetrofitInstance {
    private const val BASE_URL = "https://api.odcloud.kr/api/"

    val api: PetFacilityService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PetFacilityService::class.java)
    }
}
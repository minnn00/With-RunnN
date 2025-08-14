package com.with_runn.tmap

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmapRetrofit {
    private const val BASE_URL = "https://apis.openapi.sk.com/"

    fun createService(): TmapDirectionsService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val acceptJson = Interceptor { chain ->
            val req = chain.request().newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(req)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(acceptJson)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(TmapDirectionsService::class.java)
    }
}
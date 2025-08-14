package com.with_runn.data.api

import com.with_runn.data.LikedCoursesResponse
import com.with_runn.data.ScrapResponseWrapper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface MyPageApiService {

    @GET("api/users/scraps")
    suspend fun getScrapCourses(
        @Header("Authorization") token: String
    ): Response<ScrapResponseWrapper>

    @GET("api/users/likes")
    suspend fun getLikedCourses(
        @Header("Authorization") token: String
    ): Response<LikedCoursesResponse>
}
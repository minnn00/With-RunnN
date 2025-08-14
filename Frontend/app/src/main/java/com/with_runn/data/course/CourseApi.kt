package com.with_runn.data.course

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CourseApi {
    @GET("api/course/detail")
    suspend fun getCourseDetail(
        @Header("Authorization") accessToken: String,
        @Query("courseId") courseId: Int
    ) : CourseDetailResponse
}
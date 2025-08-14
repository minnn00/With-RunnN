package com.with_runn.data.course

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CourseApi {
    @GET("api/course/detail")
    suspend fun getCourseDetail(
        @Header("Authorization") accessToken: String,
        @Query("courseId") courseId: Int
    ) : CourseDetailResponse

    @POST("api/maps/courses")
    suspend fun createCourse(
        @Header("Authorization") accessToken: String,
        @Body body: CreateCourseRequest
    ): Response<CreateCourseResponse>
}
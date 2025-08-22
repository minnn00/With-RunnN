package com.with_runn.data.course

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApi {
    @GET("api/course/detail")
    suspend fun getCourseDetail(
        @Header("Authorization") accessToken: String,
        @Query("courseId") courseId: Int
    ) : CourseDetailResponse

    @Multipart
    @POST("api/maps/courses")
    suspend fun createCourse(
        @Header("Authorization") accessToken: String,
        @Part("courseCreateRequest") request: CreateCourseRequest,   // DTO를 JSON으로 직렬화한 파트
        @Part image: MultipartBody.Part?
    ): Response<CreateCourseResponse>

    @GET("api/course/rising")
    suspend fun getRisingCourses(
        @Header("Authorization") accessToken: String
    ): List<CourseSummary>

    @GET("api/course/rising/search")
    suspend fun searchRisingCourses(
        @Header("Authorization") accessToken: String,
        @Query("keyword") keyword: String? = null
    ): List<CourseSummary>

    @GET("api/course/nearby")
    suspend fun getNearbyCourses(
        @Header("Authorization") accessToken: String,
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null
    ): List<CourseSummary>

    @GET("api/course/nearby/search")
    suspend fun searchNearbyCourses(
        @Header("Authorization") accessToken: String,
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null,
        @Query("keyword") keyword: String? = null
    ): List<CourseSummary>

    @PATCH("api/users/courses/{course_id}")
    suspend fun updateCourse(
        @Header("Authorization") accessToken: String,
        @Path("course_id") courseId: Int,
        @Body body: UpdateCourseRequest
    ): Response<UpdateCourseResponse>
}
package com.with_runn.data.course

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CourseActionService {

    // 좋아요 등록
    @POST("api/course/like")
    suspend fun likeCourse(
        @Query("courseId") courseId: Int,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>

    // 좋아요 취소
    @DELETE("api/course/like")
    suspend fun unlikeCourse(
        @Query("courseId") courseId: Int,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>

    // 북마크 등록
    @POST("api/course/scrap")
    suspend fun bookmarkCourse(
        @Query("courseId") courseId: Int,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>

    // 북마크 취소
    @DELETE("api/course/scrap")
    suspend fun unbookmarkCourse(
        @Query("courseId") courseId: Int,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>
}
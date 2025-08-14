package com.with_runn.data.api

import com.with_runn.data.CourseDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.with_runn.data.LikeRequest
import com.with_runn.data.ScrapRequest
import com.with_runn.data.ShareRequest
import com.with_runn.data.Friend
import com.with_runn.data.LikeResponse
import com.with_runn.data.NeighborhoodPreviewResponse
import com.with_runn.data.RisingCourseResponse
import com.with_runn.data.RisingPreviewResponse
import com.with_runn.data.ScrapResponse
import com.with_runn.data.ShareResponse
import com.with_runn.data.WalkCourseResponse
import retrofit2.http.DELETE
import retrofit2.http.Header

interface CourseApi {

    @GET("api/course/nearby/preview")
    suspend fun getNeighborhoodPreview(
        @Header("Authorization") token: String,
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null
    ): Response<List<NeighborhoodPreviewResponse>>

    @GET("api/course/rising/preview")
    suspend fun getRisingPreview(
        @Header("Authorization") token: String
    ): Response<List<RisingPreviewResponse>>

    @GET("api/course/nearby")
    suspend fun getNearbyCourses(
        @Header("Authorization") token: String,
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null
    ): Response<List<NeighborhoodPreviewResponse>>

    @GET("api/course/rising")
    suspend fun getRisingCourses(
        @Header("Authorization") token: String
    ): Response<List<NeighborhoodPreviewResponse>>

    @GET("api/course/detail")
    suspend fun getCourseDetail(
        @Header("Authorization") token: String,
        @Query("courseId") courseId: Int
    ): Response<CourseDetailResponse>

    @POST("api/course/like")
    suspend fun postLike(
        @Header("Authorization") token: String,
        @Query("courseId") courseId: Int
    ): Response<LikeResponse>

    @POST("api/course/scrap")
    suspend fun postScrap(
        @Header("Authorization") token: String,
        @Query("courseId") courseId: Int
    ): Response<ScrapResponse>

    @DELETE("api/course/scrap")
    suspend fun deleteScrap(
        @Header("Authorization") token: String,
        @Query("courseId") courseId: Int
    ): Response<DeleteScrapResponse>

    @POST("api/chat/share")
    suspend fun postShareCourse(
        @Header("Authorization") token: String,
        @Body body: ShareRequest
    ): Response<ShareResponse>

    @GET("api/course/nearby/search")
    suspend fun searchNearbyCourses(
        @Header("Authorization") token: String,
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int?,
        @Query("townId") townId: Int?,
        @Query("keyword") keyword: String
    ): Response<List<NeighborhoodPreviewResponse>>

    @GET("api/course/rising/search")
    suspend fun searchRisingCourses(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String
    ): Response<List<RisingCourseResponse>>
}

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

interface CourseApi {
    @GET("api/courses/nearby/preview")
    suspend fun getNeighborhoodPreview(
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null
    ): Response<List<NeighborhoodPreviewResponse>>

    @GET("api/course/rising/preview")
    suspend fun getRisingPreview(): Response<List<RisingPreviewResponse>>


    @GET("api/course/nearby")
    suspend fun getNearbyCourses(
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null
    ): Response<List<NeighborhoodPreviewResponse>>


    @GET("api/course/rising")
    suspend fun getRisingCourses(): Response<List<NeighborhoodPreviewResponse>>


    @GET("api/course/detail")
    suspend fun getCourseDetail(
        @Query("courseId") courseId: Int
    ): Response<CourseDetailResponse>

    @POST("api/course/like")
    suspend fun postLike(
        @Query("courseId") courseId: Int
    ): Response<LikeResponse>


    @POST("api/course/scrap")
    suspend fun postScrap(
        @Query("courseId") courseId: Int
    ): Response<ScrapResponse>


    @DELETE("api/course/scrap")
    suspend fun deleteScrap(
        @Query("courseId") courseId: Int
    ): Response<DeleteScrapResponse>


    @POST("api/chat/share")
    suspend fun postShareCourse(@Body body: ShareRequest): Response<ShareResponse>

    @GET("api/course/nearby/search")
    suspend fun searchNearbyCourses(
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int?,
        @Query("townId") townId: Int?,
        @Query("keyword") keyword: String
    ): Response<List<NeighborhoodPreviewResponse>>

    @GET("api/course/rising/search")
    suspend fun searchRisingCourses(
        @Query("keyword") keyword: String
    ): Response<List<RisingCourseResponse>>

}
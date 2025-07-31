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
import com.with_runn.data.WalkCourse
import com.with_runn.ui.course.LocalMoreFragment
import com.with_runn.data.Friend
import com.with_runn.data.LikeResponse
import com.with_runn.data.ScrapResponse
import retrofit2.http.DELETE

interface CourseApi {
    @GET("api/courses/neighborhood/preview")
    suspend fun getNeighborhoodPreview(): Response<List<LocalMoreFragment>>

    @GET("api/courses/rising/preview")
    suspend fun getRisingPreview(): Response<List<LocalMoreFragment>>

    @GET("api/courses/neighborhood")
    suspend fun getNeighborhoodCourses(): Response<List<WalkCourse>>

    @GET("api/courses/rising")
    suspend fun getRisingCourses(): Response<List<WalkCourse>>

    @GET("/api/course/detail")
    suspend fun getCourseDetail(
        @Query("courseId") courseId: Int
    ): Response<CourseDetailResponse>

    @POST("api/course/like")
    suspend fun postLike(@Body request: LikeRequest): Response<LikeResponse>


    @POST("api/course/scrap")
    suspend fun postScrap(@Body body: ScrapRequest): Response<ScrapResponse>


    @DELETE("api/course/scrap")
    suspend fun deleteScrap(
        @Query("courseId") courseId: Int
    ): Response<ScrapResponse>


    @GET("api/users/friends")
    suspend fun getFriendList(): Response<List<Friend>>

    @POST("api/chat/share")
    suspend fun postShareCourse(@Body shareRequest: ShareRequest): Response<Unit>
}
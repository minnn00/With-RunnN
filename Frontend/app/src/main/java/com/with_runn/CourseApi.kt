package com.with_runn

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.with_runn.model.request.LikeRequest
import com.with_runn.model.request.ScrapRequest
import com.with_runn.model.request.ShareRequest
import com.with_runn.WalkCourse
import com.with_runn.CourseDetailFragment

interface CourseApi {
    @GET("api/courses/neighborhood/preview")
    suspend fun getNeighborhoodPreview(): Response<List<LocalMoreFragment>>

    @GET("api/courses/rising/preview")
    suspend fun getRisingPreview(): Response<List<LocalMoreFragment>>

    @GET("api/courses/neighborhood")
    suspend fun getNeighborhoodCourses(): Response<List<WalkCourse>>

    @GET("api/courses/rising")
    suspend fun getRisingCourses(): Response<List<WalkCourse>>

    @GET("api/courses/detail")
    suspend fun getCourseDetail(@Query("courseId") courseId: Int): Response<CourseDetailFragment>

    @POST("api/courses/like")
    suspend fun postLike(@Body likeRequest: LikeRequest): Response<Unit>

    @POST("api/courses/scrap")
    suspend fun postScrap(@Body scrapRequest: ScrapRequest): Response<Unit>

    @GET("api/users/friends")
    suspend fun getFriendList(): Response<List<Friend>>

    @POST("api/chat/share")
    suspend fun postShareCourse(@Body shareRequest: ShareRequest): Response<Unit>
}

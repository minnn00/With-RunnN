package com.with_runn.share

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ShareApi {
    @POST("api/chat/share")
    suspend fun shareCourse(
        @Header("Authorization") bearerToken: String,
        @Body body: ShareCourseRequest
    ): Response<Unit>
}
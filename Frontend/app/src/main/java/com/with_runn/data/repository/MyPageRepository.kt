package com.with_runn.data.repository

import com.with_runn.data.LikedCoursesResponse
import com.with_runn.data.api.MyPageApiService
import retrofit2.Response

class MyPageRepository(private val apiService: MyPageApiService) {

    suspend fun getScrapCourses(token: String) =
        apiService.getScrapCourses(token)

    suspend fun getLikedCourses(token: String): Response<LikedCoursesResponse> {
        return apiService.getLikedCourses(token)
    }

}
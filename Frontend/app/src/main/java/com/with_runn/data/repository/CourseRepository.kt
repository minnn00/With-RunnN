package com.with_runn.data.repository

import com.with_runn.data.CourseDetailResponse
import com.with_runn.data.remote.RetrofitInstance
import com.with_runn.data.LikeRequest
import com.with_runn.data.LikeResponse
import com.with_runn.data.ScrapRequest
import com.with_runn.data.ScrapResponse
import com.with_runn.data.ShareRequest
import retrofit2.Response



class CourseRepository {
    private val api = RetrofitInstance.courseApi

    suspend fun getNeighborhoodPreview() =
        RetrofitInstance.courseApi.getNeighborhoodPreview()

    suspend fun getRisingPreview() =
        RetrofitInstance.courseApi.getRisingPreview()

    suspend fun getNeighborhoodCourses() =
        RetrofitInstance.courseApi.getNeighborhoodCourses()

    suspend fun getRisingCourses() =
        RetrofitInstance.courseApi.getRisingCourses()

    suspend fun getCourseDetail(courseId: Int): CourseDetailResponse? {
        return try {
            val response = api.getCourseDetail(courseId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    suspend fun postLike(body: LikeRequest): Response<LikeResponse> {
        return api.postLike(body)
    }

    suspend fun postScrap(body: ScrapRequest): Response<ScrapResponse> {
        return RetrofitInstance.courseApi.postScrap(body)
    }


    suspend fun deleteScrap(courseId: Int): Response<ScrapResponse> {
        return RetrofitInstance.courseApi.deleteScrap(courseId)
    }

    suspend fun getFriendList() =
        RetrofitInstance.courseApi.getFriendList()

    suspend fun postShareCourse(body: ShareRequest) =
        RetrofitInstance.courseApi.postShareCourse(body)
}
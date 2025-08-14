package com.with_runn.data.repository

import android.util.Log
import com.with_runn.data.CourseDetailResponse
import com.with_runn.data.remote.RetrofitInstance
import com.with_runn.data.LikeRequest
import com.with_runn.data.LikeResponse
import com.with_runn.data.NeighborhoodPreviewResponse
import com.with_runn.data.RisingPreviewResponse
import com.with_runn.data.ScrapRequest
import com.with_runn.data.ScrapResponse
import com.with_runn.data.ShareRequest
import com.with_runn.data.ShareResponse
import retrofit2.Response



class CourseRepository {
    private val api = RetrofitInstance.courseApi

    suspend fun getNeighborhoodPreview(token: String, provinceId: Int, cityId: Int? = null, townId: Int? = null): Response<List<NeighborhoodPreviewResponse>> {
        Log.d("API_CALL", "getNeighborhoodPreview() provinceId=$provinceId, cityId=$cityId, townId=$townId")
        Log.d("API_CALL", "Auth header len=${token.length} head=${token.take(20)}...")
        return api.getNeighborhoodPreview(token, provinceId, cityId, townId)
    }

    suspend fun getRisingPreview(token: String): List<RisingPreviewResponse>? {
        return try {
            val response = api.getRisingPreview(token)
            Log.d("CourseRepository", "서버 응답코드: ${response.code()}, body: ${response.body()}")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("CourseRepository", "API 예외: ${e.message}")
            null
        }
    }

    suspend fun getNearbyCourses(token: String, provinceId: Int, cityId: Int? = null, townId: Int? = null): Response<List<NeighborhoodPreviewResponse>> {
        return api.getNearbyCourses(token, provinceId, cityId, townId)
    }

    suspend fun getRisingCourses(token: String): Response<List<NeighborhoodPreviewResponse>> {
        return api.getRisingCourses(token)
    }

    suspend fun getCourseDetail(token: String, courseId: Int): CourseDetailResponse? {
        return try {
            val response = api.getCourseDetail(token, courseId)
            Log.d("CourseRepository", "Retrofit 응답 코드: ${response.code()}, 에러: ${response.errorBody()?.string()}")
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CourseRepository", "Exception: ${e.message}", e)
            null
        }
    }

    suspend fun postLike(token: String, courseId: Int) = api.postLike(token, courseId)

    suspend fun postScrap(token: String, courseId: Int) = api.postScrap(token, courseId)

    suspend fun postShareCourse(token: String, request: ShareRequest) = api.postShareCourse(token, request)

    suspend fun deleteScrap(token: String, courseId: Int) = api.deleteScrap(token, courseId)

    suspend fun searchNearbyCourses(
        token: String,
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null,
        keyword: String
    ): Response<List<NeighborhoodPreviewResponse>> {
        return api.searchNearbyCourses(token, provinceId, cityId, townId, keyword)
    }

    suspend fun searchRisingCourses(token: String, keyword: String) =
        api.searchRisingCourses(token, keyword)
}
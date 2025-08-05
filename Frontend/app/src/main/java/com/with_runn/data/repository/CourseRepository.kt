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

    suspend fun getNeighborhoodPreview(provinceId: Int, cityId: Int? = null, townId: Int? = null): Response<List<NeighborhoodPreviewResponse>> {
        Log.d("API_CALL", "getNeighborhoodPreview() provinceId=$provinceId, cityId=$cityId, townId=$townId")
        return api.getNeighborhoodPreview(provinceId, cityId, townId)
    }


    suspend fun getRisingPreview(): List<RisingPreviewResponse>? {
        return try {
            val response = api.getRisingPreview()
            Log.d("CourseRepository", "서버 응답코드: ${response.code()}, body: ${response.body()}")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("CourseRepository", "API 예외: ${e.message}")
            null
        }
    }

    suspend fun getNearbyCourses(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null
    ): Response<List<NeighborhoodPreviewResponse>> {
        return api.getNearbyCourses(provinceId, cityId, townId)
    }

    suspend fun getRisingCourses(): Response<List<NeighborhoodPreviewResponse>> {
        return api.getRisingCourses()
    }


    suspend fun getCourseDetail(courseId: Int): CourseDetailResponse? {
        return try {
            val response = api.getCourseDetail(courseId)
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


    suspend fun postLike(courseId: Int) = api.postLike(courseId)

    suspend fun postScrap(courseId: Int) = api.postScrap(courseId)

    suspend fun postShareCourse(request: ShareRequest) = api.postShareCourse(request)


    suspend fun deleteScrap(courseId: Int) = api.deleteScrap(courseId)

    suspend fun searchNearbyCourses(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null,
        keyword: String
    ): Response<List<NeighborhoodPreviewResponse>> {
        return api.searchNearbyCourses(provinceId, cityId, townId, keyword)
    }

    suspend fun searchRisingCourses(keyword: String) =
        api.searchRisingCourses(keyword)

}
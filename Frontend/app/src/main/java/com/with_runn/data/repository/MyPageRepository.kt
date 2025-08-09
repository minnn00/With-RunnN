package com.with_runn.data.repository

import android.util.Log
import com.with_runn.data.LikedCoursesResponse
import com.with_runn.data.MyCourseResponse
import com.with_runn.data.ProfileResponse
import com.with_runn.data.TokenManager
import com.with_runn.data.api.MyPageApiService
import com.with_runn.data.api.CourseApi
import com.with_runn.data.CourseDetailResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MyPageRepository(
private val apiService: MyPageApiService,
private val courseApi: CourseApi
) {

    private fun bearerTokenOrNull(): String? {
        val raw = TokenManager.getAccessToken()?.trim()?.trim('"')
        if (raw.isNullOrBlank()) return null
        return "Bearer $raw"
    }

    suspend fun getScrapCourses(token: String) =
        apiService.getScrapCourses(token)

    suspend fun getLikedCourses(token: String): Response<LikedCoursesResponse> {
        return apiService.getLikedCourses(token)
    }

    suspend fun getMyCourses(): MyCourseResponse? {
        val token = TokenManager.getAccessToken()
        val response = apiService.getMyCourses("Bearer $token")
        Log.d("MyPageRepository", "Retrofit 응답 성공: ${response.isSuccessful}")
        Log.d("MyPageRepository", "HTTP 코드: ${response.code()}")
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getUserProfile(): Response<ProfileResponse> {
        val token = TokenManager.getAccessToken().orEmpty().trim().trim('"')
        android.util.Log.d("AUTH", "token(len=${token.length}, head=${token.take(12)})")
        // 토큰 비면 즉시 리턴(로그인 필요)
        if (token.isBlank()) {
            return Response.error(401, okhttp3.ResponseBody.create(null, ""))
        }
        return apiService.getUserProfile("Bearer $token")  // ✅ 단 한 번만 Bearer
    }

    suspend fun getCourseDetail(courseId: Int): Response<CourseDetailResponse> {
        val token = TokenManager.getAccessToken()
        return courseApi.getCourseDetail(
            token = "Bearer $token",
            courseId = courseId
        )
    }
}
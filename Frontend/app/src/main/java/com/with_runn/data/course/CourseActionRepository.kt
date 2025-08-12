package com.with_runn.data.course

import android.util.Log
import retrofit2.Response

class CourseActionRepository(
    private val api: CourseActionService
) {
    private fun bearer(token: String) = "Bearer $token"

    suspend fun likeCourse(courseId: Int, token: String): Boolean {
        val resp = api.likeCourse(courseId, bearer(token))
        logResponse("likeCourse(courseId=$courseId)", resp)
        return resp.isSuccessful
    }

    suspend fun unlikeCourse(courseId: Int, token: String): Boolean {
        val resp = api.unlikeCourse(courseId, bearer(token))
        logResponse("likeCourse(courseId=$courseId)", resp)
        return resp.isSuccessful
    }

    suspend fun bookmarkCourse(courseId: Int, token: String): Boolean {
        val resp = api.bookmarkCourse(courseId, bearer(token))
        logResponse("likeCourse(courseId=$courseId)", resp)
        return resp.isSuccessful
    }

    suspend fun unbookmarkCourse(courseId: Int, token: String): Boolean {
        val resp = api.unbookmarkCourse(courseId, bearer(token))
        logResponse("likeCourse(courseId=$courseId)", resp)
        return resp.isSuccessful
    }

    private fun <T> logResponse(action: String, resp: Response<T>) {
        if (resp.isSuccessful) {
            Log.d("DEBUG", "$action success (code=${resp.code()}, message=${resp.message()})")
        } else {
            Log.e("DEBUG", "$action failed (code=${resp.code()}, message=${resp.message()})")
            runCatching { resp.errorBody()?.string() }
                .onSuccess { body ->
                    if (!body.isNullOrBlank()) {
                        Log.e("DEBUG", "$action errorBody=${body.take(500)}")
                    }
                }
        }
    }
}
package com.with_runn.data.course

class CourseActionRepository(
    private val api: CourseActionService
) {
    private fun bearer(token: String) = "Bearer $token"

    suspend fun likeCourse(courseId: Int, token: String): Boolean {
        val resp = api.likeCourse(courseId, bearer(token))
        return resp.isSuccessful
    }

    suspend fun unlikeCourse(courseId: Int, token: String): Boolean {
        val resp = api.unlikeCourse(courseId, bearer(token))
        return resp.isSuccessful
    }

    suspend fun bookmarkCourse(courseId: Int, token: String): Boolean {
        val resp = api.bookmarkCourse(courseId, bearer(token))
        return resp.isSuccessful
    }

    suspend fun unbookmarkCourse(courseId: Int, token: String): Boolean {
        val resp = api.unbookmarkCourse(courseId, bearer(token))
        return resp.isSuccessful
    }
}
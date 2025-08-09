package com.with_runn.data.course

class CourseFetchRepository (private val api: CourseApi){
    suspend fun getCourseDetail(courseId: Int, accessToken: String): CourseDetailResponse{
        return api.getCourseDetail("Bearer $accessToken", courseId)
    }
}
package com.with_runn.data.repository

import com.with_runn.data.remote.RetrofitInstance
import com.with_runn.data.LikeRequest
import com.with_runn.data.ScrapRequest
import com.with_runn.data.ShareRequest


class CourseRepository {

    suspend fun getNeighborhoodPreview() =
        RetrofitInstance.courseApi.getNeighborhoodPreview()

    suspend fun getRisingPreview() =
        RetrofitInstance.courseApi.getRisingPreview()

    suspend fun getNeighborhoodCourses() =
        RetrofitInstance.courseApi.getNeighborhoodCourses()

    suspend fun getRisingCourses() =
        RetrofitInstance.courseApi.getRisingCourses()

    suspend fun getCourseDetail(courseId: Int) =
        RetrofitInstance.courseApi.getCourseDetail(courseId)

    suspend fun postLike(body: LikeRequest) =
        RetrofitInstance.courseApi.postLike(body)

    suspend fun postScrap(body: ScrapRequest) =
        RetrofitInstance.courseApi.postScrap(body)

    suspend fun getFriendList() =
        RetrofitInstance.courseApi.getFriendList()

    suspend fun postShareCourse(body: ShareRequest) =
        RetrofitInstance.courseApi.postShareCourse(body)
}
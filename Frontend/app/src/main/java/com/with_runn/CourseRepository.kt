package com.with_runn.data.repository

import com.with_runn.*
import com.with_runn.data.api.RetrofitInstance
import com.with_runn.model.request.LikeRequest
import com.with_runn.model.request.ScrapRequest
import com.with_runn.model.request.ShareRequest


class CourseRepository {

    suspend fun getNeighborhoodPreview() =
        RetrofitInstance.api.getNeighborhoodPreview()

    suspend fun getRisingPreview() =
        RetrofitInstance.api.getRisingPreview()

    suspend fun getNeighborhoodCourses() =
        RetrofitInstance.api.getNeighborhoodCourses()

    suspend fun getRisingCourses() =
        RetrofitInstance.api.getRisingCourses()

    suspend fun getCourseDetail(courseId: Int) =
        RetrofitInstance.api.getCourseDetail(courseId)

    suspend fun postLike(body: LikeRequest) =
        RetrofitInstance.api.postLike(body)

    suspend fun postScrap(body: ScrapRequest) =
        RetrofitInstance.api.postScrap(body)

    suspend fun getFriendList() =
        RetrofitInstance.api.getFriendList()

    suspend fun postShareCourse(body: ShareRequest) =
        RetrofitInstance.api.postShareCourse(body)
}

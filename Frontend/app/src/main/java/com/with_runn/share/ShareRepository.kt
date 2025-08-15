package com.with_runn.share

import com.with_runn.data.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ShareRepository {
    suspend fun shareCourse(userId: Int, chatId: Int, courseId: Int): Boolean = withContext(Dispatchers.IO) {
        val token = TokenManager.getAccessToken()
        val req = ShareCourseRequest(userId = userId, chatId = chatId, courseId = courseId)
        try {
            val res = ShareService.api.shareCourse("Bearer $token", req)
            res.isSuccessful
        } catch (_: HttpException) {
            false
        } catch (_: IOException) {
            false
        }
    }
}
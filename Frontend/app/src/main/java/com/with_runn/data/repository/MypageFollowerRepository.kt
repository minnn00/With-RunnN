package com.with_runn.data.repository

import com.google.gson.Gson
import com.with_runn.data.model.FollowResponse
import com.with_runn.data.model.Follower
import com.with_runn.data.network.ApiClient
import com.with_runn.ui.friend.model.dto.FriendDetailResponse

class MypageFollowerRepository {
    suspend fun fetchFollowers(): List<Follower>? {
        return try {
            val response = ApiClient.instance.getFollowers()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.result?.followers
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun fetchFollowings(): List<Follower>? {
        return try {
            val response = ApiClient.instance.getFollowings() // 이 API 추가 필요
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.result?.followings
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getFriendDetail(userId: Int): Result<FriendDetailResponse> {
        return try {
            val response = ApiClient.instance.getFriendDetail(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 본문이 비어있습니다."))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun followUser(userId: Int): Result<FollowResponse> {
        return try {
            val response = ApiClient.instance.followUser(userId)
            val body = response.errorBody()?.string()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 본문이 비어있습니다."))
            } else {
                // 실패여도 JSON 파싱 시도
                val errorResponse = try {
                    Gson().fromJson(body, FollowResponse::class.java)
                } catch (_: Exception) {
                    null
                }
                if (errorResponse != null) {
                    // result 값을 메시지로 사용
                    Result.failure(Exception(errorResponse.result))
                } else {
                    Result.failure(Exception("서버 오류: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

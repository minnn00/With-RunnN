package com.with_runn.data.repository

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
}
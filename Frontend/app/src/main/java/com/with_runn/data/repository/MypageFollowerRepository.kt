package com.with_runn.data.repository

import com.with_runn.data.model.Follower
import com.with_runn.data.network.ApiClient

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
}
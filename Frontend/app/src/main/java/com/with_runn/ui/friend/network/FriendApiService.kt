package com.with_runn.ui.friend.network

import com.with_runn.ui.friend.model.dto.RecommendedFriendResponse
import com.with_runn.ui.friend.model.dto.FriendDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendApiService {
    @GET("/api/friends/recommendation")
    suspend fun getRecommendedFriends(
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null
    ): Response<List<RecommendedFriendResponse>>

    @GET("/api/friends/all")
    suspend fun getAllFriends(
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null
    ): Response<List<RecommendedFriendResponse>>

    @GET("/api/friends/detail")
    suspend fun getFriendDetail(
        @Query("userId") userId: Int
    ): Response<FriendDetailResponse>

    @GET("/api/friends/search")
    suspend fun searchFriends(
        @Query("provinceId") provinceId: Int,
        @Query("cityId") cityId: Int? = null,
        @Query("townId") townId: Int? = null,
        @Query("keyword") keyword: String
    ): Response<List<RecommendedFriendResponse>>

    @POST("/api/friends/follow")
    suspend fun followFriend(
        @Query("userId") userId: Int
    ): Response<String>

    @POST("/api/friends/block")
    suspend fun blockUser(
        @Query("userId") userId: Int
    ): Response<String>
} 
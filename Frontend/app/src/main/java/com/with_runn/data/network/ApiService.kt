package com.with_runn.data.network

import com.with_runn.data.model.FollowResponse
import com.with_runn.data.model.FollowerResponse
import com.with_runn.data.model.FollowingResponse
import com.with_runn.data.model.Notice
import com.with_runn.data.model.setProfileImgResponse
import com.with_runn.data.model.setProfileResponse
import com.with_runn.data.model.setProfileRequest
import com.with_runn.ui.friend.model.dto.FriendDetailResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/api/users/profile")
    fun setProfile(
        @Body request: setProfileRequest
    ): Call<setProfileResponse>

    @GET("/api/users/followers")
    suspend fun getFollowers(): Response<FollowerResponse>

    @GET("/api/users/followings")
    suspend fun getFollowings(): Response<FollowingResponse>

    @GET("api/friends/detail")
    suspend fun getFriendDetail(
        @Query("userId") userId: Int
    ): Response<FriendDetailResponse>

    @POST("api/friends/follow")
    suspend fun followUser(@Query("userId") userId: Int) : Response<FollowResponse>

    @GET("api/notice")
    suspend fun getNotices(): List<Notice>

    @Multipart
    @POST("api/users/profile/image")
    fun uploadProfileImage(
        @Part file: MultipartBody.Part
    ): Call<setProfileImgResponse>
}
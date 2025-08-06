package com.with_runn.data.network

import com.with_runn.data.model.FollowerResponse
import com.with_runn.data.model.FollowingResponse
import com.with_runn.data.model.setProfileResponse
import com.with_runn.data.model.setProfileRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/api/users/profile")
    fun setProfile(
        @Body request: setProfileRequest
    ): Call<setProfileResponse>

    @GET("/api/users/followers")
    suspend fun getFollowers(): Response<FollowerResponse>

    @GET("/api/users/followings")
    suspend fun getFollowings(): Response<FollowingResponse>
}
package com.with_runn.data.network

import com.with_runn.data.model.setProfileResponse
import com.with_runn.data.model.setProfileRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/users/profile")
    fun setProfile(
        @Body request: setProfileRequest
    ): Call<setProfileResponse>
}
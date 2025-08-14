package com.with_runn.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/users/login")
    suspend fun login(
        @Body body: LoginReq
    ): Response<ApiResponse<LoginRes>>
}
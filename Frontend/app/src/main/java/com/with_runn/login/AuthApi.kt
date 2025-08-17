package com.with_runn.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {
    @POST("api/users/login")
    suspend fun login(
        @Body body: LoginReq
    ): Response<ApiResponse<LoginRes>>
    @PATCH("api/users/")
    suspend fun deleteAccount(
        @Header("Authorization") bearerToken: String
    ): Response<AccountResponseEnvelope>
}
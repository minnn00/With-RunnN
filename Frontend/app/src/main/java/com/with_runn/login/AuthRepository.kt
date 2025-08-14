package com.with_runn.login

class AuthRepository(
    private val api: AuthApi = ApiClient.authApi
) {
    suspend fun login(email: String, id: String, idToken: String?): LoginRes? {
        val req = LoginReq(email = email, naverId = id)

        val res = api.login(req)
        return if (res.isSuccessful) {
            res.body()?.result
        } else {
            null
        }
    }
}
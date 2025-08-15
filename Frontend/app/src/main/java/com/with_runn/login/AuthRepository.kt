package com.with_runn.login

import android.util.Log
import com.with_runn.data.TokenManager

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

    suspend fun deleteAccount(): Boolean {
        val token = TokenManager.getAccessToken() ?: return false
        val bearer = "Bearer ${token.trim()}"
        Log.d("AccountRepo", "DELETE /api/users authLen=${bearer.length}")
        return try {
            val res = api.deleteAccount(bearer)
            if (res.isSuccessful) {
                val ok = (res.body()?.success == true)
                Log.d("AccountRepo", "deleteAccount 2xx success=$ok code=${res.code()}")
                ok
            } else {
                Log.w("AccountRepo", "deleteAccount HTTP ${res.code()} body=${res.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AccountRepo", "deleteAccount error=${e.message}", e)
            false
        }
    }
}
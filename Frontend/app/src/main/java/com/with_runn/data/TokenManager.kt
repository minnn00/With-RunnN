package com.with_runn.data

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    }

    fun getAccessToken(): String {
        return prefs.getString("accessToken", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMUBleGFtcGxlLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpYXQiOjE3NTM4NTEzODl9.MnvlFcRaQYOUCIIqF1TuYnrcrovMS5nmk2LpRZMaa20") ?: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMUBleGFtcGxlLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpYXQiOjE3NTM4NTEzODl9.MnvlFcRaQYOUCIIqF1TuYnrcrovMS5nmk2LpRZMaa20"
    }

    fun setAccessToken(token: String) {
        prefs.edit().putString("accessToken", token).apply()
    }
}
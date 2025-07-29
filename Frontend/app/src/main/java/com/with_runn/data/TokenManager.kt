package com.with_runn.data

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    }

    fun getAccessToken(): String {
        return prefs.getString("accessToken", "") ?: ""
    }

    fun setAccessToken(token: String) {
        prefs.edit().putString("accessToken", token).apply()
    }
}
package com.with_runn.data

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}
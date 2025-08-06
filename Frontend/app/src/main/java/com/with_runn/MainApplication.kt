package com.with_runn

import android.app.Application
import com.with_runn.data.TokenManager

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        TokenManager.init(applicationContext)
    }
}
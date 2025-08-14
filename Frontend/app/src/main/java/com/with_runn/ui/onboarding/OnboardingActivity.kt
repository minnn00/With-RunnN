package com.with_runn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.with_runn.MainActivity
import com.with_runn.data.TokenManager
import com.with_runn.databinding.ActivityOnboardingBinding
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        routeIfUnauthenticated()
    }

    private fun routeIfUnauthenticated() {
        lifecycleScope.launch {
            val token = TokenManager.getAccessToken()
            val memberId = TokenManager.getCurrentUserId()

            TokenManager.clearAll()

            val isInvalid = token.isNullOrBlank() || memberId == -1
            if (!isInvalid) {
                startActivity(Intent(this@OnboardingActivity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                })
                finish()
                return@launch
            }
        }
    }
}
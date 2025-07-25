package com.with_runn.ui.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.with_runn.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 추가 로직은 거의 필요 없음
    }
}
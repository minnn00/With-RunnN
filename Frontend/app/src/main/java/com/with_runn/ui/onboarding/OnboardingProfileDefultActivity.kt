package com.with_runn.ui.onboarding

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.with_runn.databinding.ActivityOnboardingProfileDefultBinding

class OnboardingProfileDefultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingProfileDefultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnboardingProfileDefultBinding.inflate(layoutInflater);
        setContentView(binding.root);

    }
}
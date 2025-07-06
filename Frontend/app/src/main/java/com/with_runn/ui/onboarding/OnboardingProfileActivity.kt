package com.with_runn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.with_runn.databinding.ActivityOnboardingLoginBinding
import com.with_runn.databinding.ActivityOnboardingProfileBinding

class OnboardingProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnboardingProfileBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.entryDefault.setOnClickListener {
            val intent = Intent(this, OnboardingProfileDefultActivity::class.java)
            startActivity(intent)
        }
    }
}
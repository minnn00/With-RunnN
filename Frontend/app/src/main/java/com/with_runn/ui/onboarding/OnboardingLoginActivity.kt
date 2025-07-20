package com.with_runn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.with_runn.R
import com.with_runn.databinding.ActivityMainBinding
import com.with_runn.databinding.ActivityOnboardingLoginBinding

class OnboardingLoginActivity: AppCompatActivity() {
    private lateinit var binding : ActivityOnboardingLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnboardingLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.naverLoginButton.setOnClickListener {
            val intent = Intent(this, OnboardingProfileActivity::class.java)
            startActivity(intent)
        }
        
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, OnboardingProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
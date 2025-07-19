package com.with_runn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.with_runn.databinding.ActivityOnboardingProfileBinding
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.with_runn.R

class OnboardingProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingProfileBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingProfileBinding.inflate(layoutInflater);
        setContentView(binding.root);

        preferences = getSharedPreferences("user_info", MODE_PRIVATE)
        editor = preferences.edit()

        name = preferences.getString("name","입력").toString()
        binding.defaultText.text = preferences.getString("name","입력")
        if (name!="입력"){
            binding.entryDefault.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_active)
            binding.defaultText.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
        else {
            binding.entryDefault.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_inactive)
            binding.defaultText.setTextColor(ContextCompat.getColor(this,R.color.gray_400))
        }

        binding.entryDefault.setOnClickListener {
            val intent = Intent(this, OnboardingProfileDefultActivity::class.java)
            startActivity(intent)
        }
        binding.entryPersonality.setOnClickListener {
            val intent = Intent(this, OnboardingProfilePersonalityActivity::class.java)
            startActivity(intent)
        }
        binding.entryWalkingStyle.setOnClickListener {
            val intent = Intent(this, OnboardingProfileWalkingStyleActivity::class.java)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
package com.with_runn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.with_runn.databinding.ActivityMainBinding
import com.with_runn.ui.mypage.MypageFollowersActivity
import com.with_runn.ui.onboarding.OnboardingActivity
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment;
        val navController = navHost.navController;

        binding.bottomNavigationView.setupWithNavController(navController);

        //버튼 클릭 시 OnboardingActivity로 이동
        val button: Button = binding.startOnboardingActivityButton
        button.setOnClickListener {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
        }

        binding.startMypageFollowersActivityButton.setOnClickListener {
            val intent = Intent(this, MypageFollowersActivity::class.java)
            startActivity(intent)
        }
    }
}
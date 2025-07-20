package com.with_runn.ui.mypage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.with_runn.databinding.ActivityMypageFollowersBinding

class MypageFollowersActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMypageFollowersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageFollowersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager2
        val tabLayout = binding.tabLayout

        val adapter = MypageFollowersFragmentAdaper(this)
        viewPager.adapter = adapter

        val tabTitles = listOf("팔로워", "팔로우")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.backButton.setOnClickListener {
            finish()
        }

    }
}
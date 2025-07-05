package com.with_runn.ui.friends

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.with_runn.databinding.ActivityFriendsBinding
import com.with_runn.ui.chat.ChatListFragment
import com.with_runn.ui.location.LocationActivity

class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        setupClickListeners()
    }

    private fun setupViewPager() {
        val adapter = FriendsTabAdapter()
        binding.viewPager.adapter = adapter
        
        // 탭 간 스와이프 기능 비활성화
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "추천 친구"
                1 -> "모두 보기"
                else -> ""
            }
        }.attach()
    }

    private fun setupClickListeners() {
        binding.apply {
            // 위치 클릭 - LocationActivity로 이동
            llLocation.setOnClickListener {
                val intent = Intent(this@FriendsActivity, LocationActivity::class.java)
                startActivity(intent)
            }

            // 알림 클릭 - 임시로 토스트 메시지
            ivNotifications.setOnClickListener {
                // TODO: 알림 기능 구현
            }

            // 채팅 클릭 - 임시로 ChatListFragment를 새로운 Activity로 이동
            ivChat.setOnClickListener {
                // TODO: 채팅 목록 기능 구현
            }
        }
    }

    private inner class FriendsTabAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): androidx.fragment.app.Fragment {
            return when (position) {
                0 -> RecommendedFriendsFragment()
                1 -> FriendsSeeAllFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
} 
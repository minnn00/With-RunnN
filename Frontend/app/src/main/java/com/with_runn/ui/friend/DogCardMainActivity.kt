package com.with_runn.ui.friend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.with_runn.R
import com.with_runn.databinding.ActivityDogCardMainBinding
import com.with_runn.ui.chat.activity.ChatActivity

class DogCardMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDogCardMainBinding
    private lateinit var friendTabAdapter: FriendTabAdapter
    
    private val tabTitles = listOf("추천 친구", "모두 보기")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 시스템 UI 설정
        setupSystemUI()

        binding = ActivityDogCardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabLayout()
        setupChatButton()
        setupBottomNavigation()
    }

    private fun setupTabLayout() {
        friendTabAdapter = FriendTabAdapter(this)
        binding.viewPager.adapter = friendTabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupChatButton() {
        // 채팅 버튼 클릭 이벤트
        binding.chatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        // 바텀 네비게이션 설정 - 현재는 단순히 메뉴만 설정
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.friend_graph -> {
                    // 이미 친구 화면이므로 아무것도 하지 않음
                    true
                }
                R.id.course_graph -> {
                    // 코스 화면으로 이동 (필요시 구현)
                    true
                }
                R.id.map_graph -> {
                    // 지도 화면으로 이동 (필요시 구현)
                    true
                }
                R.id.mypage_graph -> {
                    // 마이페이지 화면으로 이동 (필요시 구현)
                    true
                }
                else -> false
            }
        }
    }



    /**
     * 채팅 탭으로 이동하는 메서드
     */
    fun navigateToChatTab() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun setupSystemUI() {
        // WindowCompat를 사용한 현대적인 시스템 UI 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // 시스템 UI 컨트롤러 설정
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.isAppearanceLightNavigationBars = true
    }
} 
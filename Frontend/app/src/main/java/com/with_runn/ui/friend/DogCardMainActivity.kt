package com.with_runn.ui.friend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.with_runn.R
import com.with_runn.databinding.ActivityDogCardMainBinding
import com.with_runn.ui.chat.activity.ChatActivity
import com.with_runn.ui.friend.RecommendedFriendsFragment
import com.with_runn.ui.friend.AllFriendsFragment

class DogCardMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDogCardMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 시스템 UI 설정
        setupSystemUI()

        binding = ActivityDogCardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabClickListeners()
        setupChatButton()
        setupBottomNavigation()
        
        // 기본적으로 추천 친구 Fragment 표시
        showRecommendedFriendsFragment()
    }

    private fun setupTabClickListeners() {
        // 추천 친구 탭 클릭 이벤트
        binding.recommendedTab.setOnClickListener {
            showRecommendedFriendsFragment()
            updateTabUI(true)
        }

        // 모두 보기 탭 클릭 이벤트
        binding.allFriendsTab.setOnClickListener {
            showAllFriendsFragment()
            updateTabUI(false)
        }
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

    private fun showRecommendedFriendsFragment() {
        val fragment = RecommendedFriendsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showAllFriendsFragment() {
        val fragment = AllFriendsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateTabUI(isRecommendedActive: Boolean) {
        if (isRecommendedActive) {
            // 추천 친구 탭 활성화
            binding.recommendedTabText.apply {
                setTextColor(resources.getColor(R.color.black, null))
                textSize = 16f
            }
            binding.recommendedTabIndicator.visibility = View.VISIBLE

            // 모두 보기 탭 비활성화
            binding.allFriendsTabText.apply {
                setTextColor(resources.getColor(R.color.gray_400, null))
                textSize = 16f
            }
            binding.allFriendsTabIndicator.visibility = View.GONE
        } else {
            // 모두 보기 탭 활성화
            binding.allFriendsTabText.apply {
                setTextColor(resources.getColor(R.color.black, null))
                textSize = 16f
            }
            binding.allFriendsTabIndicator.visibility = View.VISIBLE

            // 추천 친구 탭 비활성화
            binding.recommendedTabText.apply {
                setTextColor(resources.getColor(R.color.gray_400, null))
                textSize = 16f
            }
            binding.recommendedTabIndicator.visibility = View.GONE
        }
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
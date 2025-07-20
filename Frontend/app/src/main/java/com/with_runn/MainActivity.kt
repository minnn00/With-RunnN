package com.with_runn

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.with_runn.adapter.DogCard
import com.with_runn.adapter.DogCardAdapter
import com.with_runn.databinding.ActivityMainBinding
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dogCardAdapter: DogCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 시스템 UI 설정
        setupSystemUI()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupTabClickListeners()
        setupChatButton()
    }
    
    private fun setupViewPager() {
        // 4개의 강아지 카드 데이터 생성 (모두 jonny.png 사용!)
        val dogCards = listOf(
            DogCard(
                name = "조니",
                tag = "#에너지 폭발",
                imageResId = R.drawable.jonny, // jonny.png 사용
                tags = listOf("#에너지 폭발", "#강아지 친구 찾기형")
            ),
            DogCard(
                name = "밀리",
                tag = "#호기심 왕성",
                imageResId = R.drawable.jonny, // jonny.png 사용
                tags = listOf("#차분함", "#고집 셈")
            ),
            DogCard(
                name = "호두",
                tag = "#그치만안물어요",
                imageResId = R.drawable.jonny, // jonny.png 사용
                tags = listOf("#낯가림", "#방어적")
            ),
            DogCard(
                name = "루시",
                tag = "#활발함",
                imageResId = R.drawable.jonny, // jonny.png 사용
                tags = listOf("#친화적", "#장난기 많음")
            )
        )
        
        dogCardAdapter = DogCardAdapter(dogCards)
        binding.cardViewpager.adapter = dogCardAdapter
        
        // 페이지 변경 리스너 추가
        binding.cardViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updatePaginationIndicators(position)
            }
        })

        // 카드 클릭 리스너 추가
        dogCardAdapter.setOnItemClickListener { position ->
            val dogCard = dogCards[position]
            val intent = Intent(this, FriendDetailActivity::class.java).apply {
                putExtra("dog_name", dogCard.name)
                putExtra("dog_age", when(position) {
                    0 -> "3년 6개월"
                    1 -> "2년 3개월"
                    2 -> "4년 1개월"
                    3 -> "1년 8개월"
                    else -> "3년 6개월"
                })
                putExtra("dog_breed", when(position) {
                    0 -> "래브라도 리트리버"
                    1 -> "골든 리트리버"
                    2 -> "허스키"
                    3 -> "보더 콜리"
                    else -> "래브라도 리트리버"
                })
                putExtra("dog_category", when(position) {
                    0 -> "대형견"
                    1 -> "대형견"
                    2 -> "중형견"
                    3 -> "중형견"
                    else -> "대형견"
                })
                putExtra("dog_intro", when(position) {
                    0 -> "안녕하세요! 달리기 좋아하는 3살 조니예요 :)"
                    1 -> "호기심 많은 밀리입니다! 새로운 친구 만나고 싶어요~"
                    2 -> "조용한 호두입니다. 천천히 친해져요 :)"
                    3 -> "활발한 루시예요! 함께 놀아요!"
                    else -> "안녕하세요! 달리기 좋아하는 3살 조니예요 :)"
                })
            }
            startActivity(intent)
        }
    }
    
    private fun updatePaginationIndicators(selectedPosition: Int) {
        val indicator0 = findViewById<ImageView>(R.id.indicator_0)
        val indicator1 = findViewById<ImageView>(R.id.indicator_1)
        val indicator2 = findViewById<ImageView>(R.id.indicator_2)
        val indicator3 = findViewById<ImageView>(R.id.indicator_3)
        val indicators = listOf(indicator0, indicator1, indicator2, indicator3)
        indicators.forEachIndexed { idx, imageView ->
            imageView.setImageResource(
                if (idx == selectedPosition) R.drawable.ic_indicator_active
                else R.drawable.ic_indicator_inactive
            )
            }
    }
    
    private fun setupTabClickListeners() {
        // 모두 보기 탭 클릭 이벤트
        findViewById<LinearLayout>(R.id.all_friends_tab).setOnClickListener {
            val intent = Intent(this, AllFriendsActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun setupChatButton() {
        // 채팅 버튼 클릭 이벤트
        findViewById<ImageView>(R.id.chat_button).setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun setupSystemUI() {
        // WindowCompat를 사용한 현대적인 시스템 UI 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // 시스템 UI 컨트롤러 설정
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.isAppearanceLightNavigationBars = true
        
        // 최신 API를 사용하여 상태바와 네비게이션바를 투명하게 설정
        window.insetsController?.let { controller ->
            controller.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
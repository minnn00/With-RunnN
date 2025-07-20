package com.with_runn.friend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.with_runn.R

class FriendDetailActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 시스템 UI 설정
        setupSystemUI()
        
        setContentView(R.layout.activity_friend_detail)
        
        // 전달받은 데이터 설정
        setupDogInfo()
        
        // 버튼 클릭 리스너 설정
        setupButtonListeners()
    }
    
    private fun setupDogInfo() {
        // 전달받은 강아지 정보 설정
        val dogName = intent.getStringExtra("dog_name") ?: "조니"
        val dogAge = intent.getStringExtra("dog_age") ?: "3년 6개월"
        val dogBreed = intent.getStringExtra("dog_breed") ?: "래브라도 리트리버"
        val dogCategory = intent.getStringExtra("dog_category") ?: "대형견"
        val dogIntro = intent.getStringExtra("dog_intro") ?: "안녕하세요! 달리기 좋아하는 3살 조니예요 :)"
        
        findViewById<TextView>(R.id.dog_name).text = dogName
        findViewById<TextView>(R.id.dog_age).text = dogAge
        findViewById<TextView>(R.id.dog_breed).text = dogBreed
        findViewById<TextView>(R.id.dog_category).text = dogCategory
        findViewById<TextView>(R.id.dog_intro).text = dogIntro
        
        // jonny.png는 그대로 사용
        findViewById<ImageView>(R.id.dog_image).setImageResource(R.drawable.jonny)
        findViewById<ImageView>(R.id.profile_image).setImageResource(R.drawable.jonny)
    }
    
    private fun setupButtonListeners() {
        // 팔로우 버튼
        findViewById<TextView>(R.id.follow_button).setOnClickListener {
            // 팔로우 기능 구현
        }
        
        // 메시지 버튼
        findViewById<TextView>(R.id.message_button).setOnClickListener {
            // 메시지 기능 구현
        }
        
        // 메뉴 버튼
        findViewById<ImageView>(R.id.menu_button).setOnClickListener {
            Log.d("FriendDetailActivity", "메뉴 버튼 클릭됨")
            showMenuPopup(it)
        }
        
        // 추천 친구 탭
        findViewById<View>(R.id.recommended_tab).setOnClickListener {
            Log.d("FriendDetailActivity", "추천 친구 탭 클릭됨")
            // 현재 화면이므로 아무것도 하지 않음
        }
        
        // 모두 보기 탭
        findViewById<View>(R.id.all_friends_tab).setOnClickListener {
            Log.d("FriendDetailActivity", "모두 보기 탭 클릭됨")
            // AllFriendsActivity로 이동
            val intent = Intent(this, AllFriendsActivity::class.java)
            startActivity(intent)
        }
        
        // TextView에도 클릭 이벤트 추가 (백업용)
        findViewById<TextView>(R.id.all_friends_text).setOnClickListener {
            Log.d("FriendDetailActivity", "모두 보기 TextView 클릭됨")
            val intent = Intent(this, AllFriendsActivity::class.java)
            startActivity(intent)
        }
        
        // 탭 컨테이너에도 클릭 이벤트 추가 (디버깅용)
        findViewById<View>(R.id.tab_container).setOnClickListener {
            Log.d("FriendDetailActivity", "탭 컨테이너 클릭됨")
        }
    }
    
    private fun showMenuPopup(anchorView: View) {
        Log.d("FriendDetailActivity", "showMenuPopup 호출됨")
        
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_menu, null)
        
        // 팝업 뷰의 크기를 측정
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        
        Log.d("FriendDetailActivity", "팝업 크기: ${popupView.measuredWidth} x ${popupView.measuredHeight}")
        
        val popupWindow = PopupWindow(
            popupView,
            popupView.measuredWidth,
            popupView.measuredHeight
        )
        
        // 팝업 윈도우 설정
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 10f
        
        // 팝업 표시 (메뉴 버튼 우측 상단)
        popupWindow.showAsDropDown(anchorView, -popupWindow.width + anchorView.width, 0)
        
        Log.d("FriendDetailActivity", "팝업 표시됨")
        
        // 팝업 메뉴 아이템 클릭 리스너
        popupView.findViewById<View>(R.id.block_button).setOnClickListener {
            Log.d("FriendDetailActivity", "차단하기 클릭됨")
            // 차단하기 기능 구현
            popupWindow.dismiss()
        }
        
        popupView.findViewById<View>(R.id.report_button).setOnClickListener {
            Log.d("FriendDetailActivity", "신고하기 클릭됨")
            // 신고하기 기능 구현
            popupWindow.dismiss()
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
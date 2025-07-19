package com.with_runn

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class ChatActivity : AppCompatActivity() {
    
    private lateinit var chatAdapter: ChatAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 시스템 UI 설정
        setupSystemUI()
        
        setContentView(R.layout.activity_chat)
        
        // 뒤로가기 버튼 설정
        setupBackButton()
        
        // RecyclerView 설정
        setupRecyclerView()
        
        // 샘플 데이터 로드
        loadSampleData()
    }
    
    private fun setupBackButton() {
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.chat_list)
        
        // LinearLayoutManager 설정
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        
        // 어댑터 설정
        chatAdapter = ChatAdapter()
        recyclerView.adapter = chatAdapter
        
        // 채팅방 클릭 리스너 설정
        chatAdapter.setOnItemClickListener { chatRoom ->
            // 채팅방 클릭 시 처리 (나중에 구현)
        }
    }
    
    private fun loadSampleData() {
        val chatRooms = listOf(
            ChatRoom(
                name = "조니",
                time = "14:20",
                lastMessage = "좋아요! 그럼 6시에 연남에서 보는 거 어떠세요?",
                notificationCount = 1,
                profileImageResId = R.drawable.jonny
            ),
            ChatRoom(
                name = "마루",
                time = "10:00",
                lastMessage = "산책 즐거웠어요 ~ 다음에 또 같이 해요~!",
                profileImageResId = R.drawable.maru
            ),
            ChatRoom(
                name = "이름 없는 사용자",
                time = "25.05.29",
                lastMessage = "저기요 제 개껌 돌려달라고요",
                profileImageResId = R.drawable.guri
            ),
            ChatRoom(
                name = "초코, 모찌",
                time = "25.05.12",
                lastMessage = "네 좋아요 ~ ^^",
                notificationCount = 3,
                hasSecondImage = true,
                profileImageResId = R.drawable.ellipse_52,
                profileImage2ResId = R.drawable.ellipse_50
            ),
            ChatRoom(
                name = "마루",
                time = "25.05.09",
                lastMessage = "간식 감사합니다! 담에 또 같이 산책해요 ㅎㅎ",
                profileImageResId = R.drawable.maru
            )
        )
        
        chatAdapter.updateChatRooms(chatRooms)
    }
    
    private fun setupSystemUI() {
        // WindowCompat를 사용한 현대적인 시스템 UI 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // 상태바와 네비게이션바를 투명하게 설정
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        
        // 시스템 UI 컨트롤러 설정
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.isAppearanceLightNavigationBars = true
    }
} 
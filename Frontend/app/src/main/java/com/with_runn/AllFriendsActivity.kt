package com.with_runn

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class AllFriendsActivity : AppCompatActivity() {
    
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var allFriends: List<Friend>
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchButton: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 시스템 UI 설정
        setupSystemUI()
        
        setContentView(R.layout.activity_all_friends)
        
        // 검색 UI 설정
        setupSearchUI()
        
        // RecyclerView 설정
        setupRecyclerView()
        
        // 샘플 데이터 로드
        loadSampleData()
        
        // 탭 클릭 이벤트 설정
        setupTabClickListeners()
        
        // 채팅 버튼 클릭 이벤트 설정
        setupChatButton()
    }
    
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.friends_grid)
        
        // 2열 그리드 레이아웃 설정
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        
        // 어댑터 설정
        friendsAdapter = FriendsAdapter()
        recyclerView.adapter = friendsAdapter
        
        // 카드 클릭 리스너 설정
        friendsAdapter.setOnItemClickListener { friend ->
            val dialogFragment = FriendProfileDialogFragment.newInstance(
                friend.name,
                friend.personalityTag,
                ArrayList(friend.personalityTags),
                friend.imageResId
            )
            dialogFragment.show(supportFragmentManager, "FriendProfileDialog")
        }
    }
    
    private fun setupSearchUI() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearSearchButton = findViewById(R.id.clear_search_button)
        
        // 검색 텍스트 변경 리스너
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val searchQuery = s.toString().trim()
                
                // 검색어가 있으면 지우기 버튼 표시
                clearSearchButton.visibility = if (searchQuery.isNotEmpty()) View.VISIBLE else View.GONE
                
                // 검색 실행
                filterFriends(searchQuery)
            }
        })
        
        // 지우기 버튼 클릭 리스너
        clearSearchButton.setOnClickListener {
            searchEditText.text.clear()
            clearSearchButton.visibility = View.GONE
            filterFriends("")
        }
    }
    
    private fun filterFriends(query: String) {
        if (query.isEmpty()) {
            // 검색어가 없으면 모든 친구 표시
            friendsAdapter.updateFriends(allFriends)
        } else {
            // 검색어로 필터링
            val filteredFriends = allFriends.filter { friend ->
                // 이름으로 검색
                friend.name.contains(query, ignoreCase = true) ||
                // 성격 태그로 검색
                friend.personalityTag.contains(query, ignoreCase = true) ||
                friend.personalityTags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
            friendsAdapter.updateFriends(filteredFriends)
        }
    }
    
    private fun loadSampleData() {
        allFriends = listOf(
            Friend("마루", "#호기심쟁이", listOf("#차분함", "#똑똑함"), R.drawable.maru),
            Friend("룽지", "#우리친해질래?", listOf("#조용함", "#사교적"), R.drawable.roongji),
            Friend("홍이", "#달리기실시", listOf("#독립적", "#에너지 폭발"), R.drawable.hongi),
            Friend("구리", "#같이놀자", listOf("#느긋함", "#스킨십좋아함"), R.drawable.guri)
        )
        
        friendsAdapter.updateFriends(allFriends)
    }
    
    private fun setupTabClickListeners() {
        // 추천 친구 탭 클릭 이벤트
        findViewById<LinearLayout>(R.id.recommended_tab).setOnClickListener {
            // MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
    
    private fun setupChatButton() {
        findViewById<ImageView>(R.id.chat_button).setOnClickListener {
            // ChatActivity로 이동
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
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

// 데이터 클래스
data class Friend(
    val name: String,
    val personalityTag: String,
    val personalityTags: List<String>,
    val imageResId: Int
) 
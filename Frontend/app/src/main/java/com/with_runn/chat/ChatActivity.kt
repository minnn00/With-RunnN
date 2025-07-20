package com.with_runn.ui.chat

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.WindowCompat
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.view.MotionEvent
import com.with_runn.R

class ChatActivity : AppCompatActivity() {
    
    private lateinit var chatAdapter: ChatAdapter
    private var chatRooms: List<ChatRoom> = listOf()
    private var currentSwipedPosition = -1
    
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
        
        // 스와이프 삭제 기능 설정
        setupSwipeToDelete()
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
            if (chatRoom.name == "조니") {
                // 조니 채팅방 클릭 시 ChatRoomActivity로 이동
                val intent = android.content.Intent(this, ChatRoomActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "${chatRoom.name} 채팅방을 클릭했습니다", Toast.LENGTH_SHORT).show()
            }
        }
        
        // 삭제 클릭 리스너 설정
        chatAdapter.setOnDeleteClickListener { chatRoom, position ->
            deleteChatRoom(position)
        }
    }
    
    private fun setupSwipeToDelete() {
        val recyclerView = findViewById<RecyclerView>(R.id.chat_list)
        
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    showDeleteButton(position)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    hideDeleteButton()
                }
            }
            
            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val deleteButton = itemView.findViewById<TextView>(R.id.delete_button)
                    val deleteBackground = itemView.findViewById<View>(R.id.delete_background)
                    val chatItemContainer = itemView.findViewById<View>(R.id.chat_item_container)
                    
                    // 삭제 버튼 크기 (카카오톡과 동일하게 80dp)
                    val deleteButtonWidth = 80f * resources.displayMetrics.density
                    
                    // 왼쪽 스와이프 (삭제 버튼 표시)
                    if (dX < 0) {
                        // 스와이프 거리를 삭제 버튼 크기로 제한
                        val limitedDX = dX.coerceAtMost(-deleteButtonWidth)
                        
                        // 스와이프 진행도 계산
                        val progress = -limitedDX / deleteButtonWidth
                        
                        if (progress > 0) {
                            // 삭제 버튼 표시
                            deleteBackground.visibility = View.VISIBLE
                            deleteButton.visibility = View.VISIBLE
                            
                            // 카카오톡처럼 자연스러운 투명도 변화
                            deleteBackground.alpha = progress.coerceIn(0f, 1f)
                            deleteButton.alpha = progress.coerceIn(0f, 1f)
                            
                            // 채팅 아이템을 왼쪽으로 밀어냄 (카카오톡과 동일)
                            chatItemContainer.translationX = limitedDX
                            
                            // 스와이프 완료 시 상태 업데이트
                            if (progress >= 1f) {
                                currentSwipedPosition = viewHolder.adapterPosition
                            }
                        }
                    } else if (dX > 0) {
                        // 오른쪽 스와이프 (삭제 버튼 숨기기) - 삭제 버튼이 표시되어 있을 때만
                        if (currentSwipedPosition == viewHolder.adapterPosition) {
                            val limitedDX = dX.coerceAtMost(deleteButtonWidth)
                            val progress = limitedDX / deleteButtonWidth
                            
                            if (progress > 0) {
                                // 카카오톡처럼 자연스러운 투명도 감소
                                deleteBackground.alpha = (1f - progress).coerceIn(0f, 1f)
                                deleteButton.alpha = (1f - progress).coerceIn(0f, 1f)
                                
                                // 채팅 아이템을 원래 위치로 복원
                                chatItemContainer.translationX = limitedDX
                                
                                // 완전히 숨겨지면 상태 리셋
                                if (progress >= 1f) {
                                    deleteBackground.visibility = View.GONE
                                    deleteButton.visibility = View.GONE
                                    currentSwipedPosition = -1
                                }
                            }
                        }
                    } else {
                        // 스와이프가 없을 때
                        if (currentSwipedPosition != viewHolder.adapterPosition) {
                            deleteBackground.visibility = View.GONE
                            deleteButton.visibility = View.GONE
                            chatItemContainer.translationX = 0f
                        }
                    }
                }
                
                // 부모 클래스의 onChildDraw를 호출하지 않아서 기본 스와이프 동작을 막음
                // super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                // 카카오톡과 동일한 스와이프 임계값
                val deleteButtonWidth = 80f * resources.displayMetrics.density
                val itemWidth = viewHolder.itemView.width.toFloat()
                return deleteButtonWidth / itemWidth
            }
            
            override fun getSwipeEscapeVelocity(defaultEscapeVelocity: Float): Float {
                // 스와이프 이스케이프 속도를 낮춰서 더 쉽게 스와이프할 수 있도록 함
                return defaultEscapeVelocity * 0.5f
            }
            
            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.adapterPosition
                
                // 현재 스와이프된 아이템이 있으면 다른 아이템은 스와이프 불가
                if (currentSwipedPosition != -1 && currentSwipedPosition != position) {
                    return 0
                }
                
                // 오른쪽 스와이프는 삭제 버튼이 표시되어 있을 때만 가능
                if (currentSwipedPosition == position) {
                    return ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                } else {
                    return ItemTouchHelper.LEFT
                }
            }
            
            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                // 스와이프가 끝났을 때 모든 요소의 translationX를 0으로 리셋
                val itemView = viewHolder.itemView
                val chatItemContainer = itemView.findViewById<View>(R.id.chat_item_container)
                val profileImage = itemView.findViewById<ImageView>(R.id.profile_image)
                val profileImage2 = itemView.findViewById<ImageView>(R.id.profile_image_2)
                val chatName = itemView.findViewById<TextView>(R.id.chat_name)
                val chatTime = itemView.findViewById<TextView>(R.id.chat_time)
                val lastMessage = itemView.findViewById<TextView>(R.id.last_message)
                val notificationBadge = itemView.findViewById<View>(R.id.notification_badge)
                val notificationCount = itemView.findViewById<TextView>(R.id.notification_count)
                
                chatItemContainer?.translationX = 0f
                profileImage?.translationX = 0f
                profileImage2?.translationX = 0f
                chatName?.translationX = 0f
                chatTime?.translationX = 0f
                lastMessage?.translationX = 0f
                notificationBadge?.translationX = 0f
                notificationCount?.translationX = 0f
                
                super.clearView(recyclerView, viewHolder)
            }
        }
        
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
        
        // 다른 곳을 터치하면 삭제 버튼 숨기기
        recyclerView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (currentSwipedPosition != -1) {
                        hideDeleteButton()
                    }
                }
            }
            false
        }
    }
    
    private fun showDeleteButton(position: Int) {
        chatAdapter.showDeleteButton(position)
        currentSwipedPosition = position
    }
    
    private fun hideDeleteButton() {
        chatAdapter.hideDeleteButton()
        currentSwipedPosition = -1
        
        // 모든 아이템의 모든 요소들의 translationX를 0으로 리셋
        val recyclerView = findViewById<RecyclerView>(R.id.chat_list)
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val chatItemContainer = child.findViewById<View>(R.id.chat_item_container)
            val profileImage = child.findViewById<ImageView>(R.id.profile_image)
            val profileImage2 = child.findViewById<ImageView>(R.id.profile_image_2)
            val chatName = child.findViewById<TextView>(R.id.chat_name)
            val chatTime = child.findViewById<TextView>(R.id.chat_time)
            val lastMessage = child.findViewById<TextView>(R.id.last_message)
            val notificationBadge = child.findViewById<View>(R.id.notification_badge)
            val notificationCount = child.findViewById<TextView>(R.id.notification_count)
            
            chatItemContainer?.translationX = 0f
            profileImage?.translationX = 0f
            profileImage2?.translationX = 0f
            chatName?.translationX = 0f
            chatTime?.translationX = 0f
            lastMessage?.translationX = 0f
            notificationBadge?.translationX = 0f
            notificationCount?.translationX = 0f
        }
    }
    
    private fun deleteChatRoom(position: Int) {
        if (position in chatRooms.indices) {
            val deletedChatRoom = chatRooms[position]
            val newChatRooms = chatRooms.toMutableList()
            newChatRooms.removeAt(position)
            chatRooms = newChatRooms
            chatAdapter.updateChatRooms(chatRooms)
            hideDeleteButton()
            
            Toast.makeText(this, "${deletedChatRoom.name} 채팅방이 삭제되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadSampleData() {
        chatRooms = listOf(
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
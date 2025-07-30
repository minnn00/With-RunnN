package com.with_runn.ui.chat.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R
import com.with_runn.ui.friend.FriendProfileDialogFragment
import com.with_runn.ui.chat.model.Message
import com.with_runn.ui.chat.adapter.ChatMessageAdapter
import com.with_runn.ui.chat.repository.ChatRepository
import com.with_runn.ui.chat.dialog.AddParticipantBottomSheet
import com.with_runn.ui.chat.dialog.ChatRoomNameSettingDialogFragment

class ChatRoomActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var chatTitle: TextView
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageAdapter: ChatMessageAdapter
    
    private var currentParticipants = mutableListOf<String>()
    private var originalFriendName = ""
    private val chatRepository = ChatRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ChatRoomActivity", "onCreate 시작")
        
        // 시스템 UI 설정
        setupSystemUI()
        
        setContentView(R.layout.activity_chat_room)
        Log.d("ChatRoomActivity", "레이아웃 설정 완료")
        
        setupViews()
        setupClickListeners()
        
        // 새로 생성된 채팅방인지 확인
        val isNewChat = intent.getBooleanExtra("is_new_chat", false)
        if (isNewChat) {
            Log.d("ChatRoomActivity", "새로 생성된 채팅방이므로 메시지를 로드하지 않습니다")
        } else {
            // 기존 채팅방인 경우에만 메시지 로드
            loadChatMessagesFromApi()
        }
        Log.d("ChatRoomActivity", "onCreate 완료")
    }
    
    private fun setupViews() {
        Log.d("ChatRoomActivity", "setupViews 시작")
        backButton = findViewById(R.id.back_button)
        menuButton = findViewById(R.id.menu_button)
        chatTitle = findViewById(R.id.chat_title)
        messageRecyclerView = findViewById(R.id.message_recycler_view)
        
        // Intent에서 친구 정보 가져오기
        originalFriendName = intent.getStringExtra("friend_name") ?: "조니"
        Log.d("ChatRoomActivity", "친구 이름: $originalFriendName")
        currentParticipants.add(originalFriendName)
        chatTitle.text = originalFriendName
        
        messageRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        messageAdapter = ChatMessageAdapter()
        messageAdapter.setOnProfileImageClickListener { sender ->
            showFriendProfileDialog(sender)
        }
        messageRecyclerView.adapter = messageAdapter
        
        // 새로 생성된 채팅방인지 확인 (messageAdapter 초기화 후)
        val isNewChat = intent.getBooleanExtra("is_new_chat", false)
        if (isNewChat) {
            Log.d("ChatRoomActivity", "새로 생성된 채팅방입니다")
            // 새 채팅방 생성 메시지 표시
            showNewChatMessage(originalFriendName)
        }
        Log.d("ChatRoomActivity", "setupViews 완료")
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
        
        menuButton.setOnClickListener {
            showMenuPopup()
        }
    }
    
    private fun showMenuPopup() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.chat_room_popup_menu, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        
        // 팝업 메뉴 위치 설정
        popupWindow.showAsDropDown(menuButton, 0, 0)
        
        // 메뉴 아이템 클릭 리스너
        popupView.findViewById<View>(R.id.add_participant_button).setOnClickListener {
            showAddParticipantBottomSheet()
            popupWindow.dismiss()
        }
        
        popupView.findViewById<View>(R.id.set_chat_name_button).setOnClickListener {
            showChatRoomNameSettingDialog()
            popupWindow.dismiss()
        }
        

    }

    private fun showAddParticipantBottomSheet() {
        val bottomSheet = AddParticipantBottomSheet()
        bottomSheet.setOnParticipantAddedListener { newParticipant ->
            addParticipantToChat(newParticipant)
        }
        bottomSheet.show(supportFragmentManager, "AddParticipantBottomSheet")
    }
    
    private fun addParticipantToChat(newParticipant: String) {
        if (!currentParticipants.contains(newParticipant)) {
            currentParticipants.add(newParticipant)
            
            // 시스템 메시지 추가
            val systemMessage = Message(
                messageId = System.currentTimeMillis().toInt(),
                sender = "시스템",
                content = "${originalFriendName}님이 ${newParticipant}님을 초대했습니다",
                timestamp = "방금 전",
                isFromMe = false,
                isSystemMessage = true
            )
            
            val currentMessages = messageAdapter.currentList.toMutableList()
            currentMessages.add(systemMessage)
            messageAdapter.submitList(currentMessages)
            
            // 스크롤을 맨 아래로
            messageRecyclerView.post {
                messageRecyclerView.smoothScrollToPosition(currentMessages.size - 1)
            }
        }
    }
    
    private fun showChatRoomNameSettingDialog() {
        val dialog = ChatRoomNameSettingDialogFragment()
        dialog.setOnNameSetListener { roomName ->
            // 채팅방 이름 설정 처리
            chatTitle.text = roomName
        }
        dialog.show(supportFragmentManager, "ChatRoomNameSettingDialog")
    }
    
    private fun showFriendProfileDialog(friendName: String) {
        val dialog = FriendProfileDialogFragment.newInstance(
            friendName = friendName,
            personalityTag = "활발한",
            personalityTags = arrayListOf("활발한", "친근한", "사교적인"),
            imageResId = R.drawable.jonny
        )
        dialog.show(supportFragmentManager, "FriendProfileDialog")
    }
    
    /**
     * API를 통해 채팅 메시지를 로드
     */
    private fun loadChatMessagesFromApi() {
        Log.d("ChatRoomActivity", "API로 메시지 로드 시작")
        
        // 임시로 chatId 1 사용 (조니와의 채팅방)
        val chatId = 1
        
        chatRepository.getChatMessages(chatId) { result ->
            result.onSuccess { messages ->
                Log.d("ChatRoomActivity", "메시지 로드 성공: ${messages.size}개")
                
                // UI 업데이트
                runOnUiThread {
                    messageAdapter.submitList(messages)
                    
                    // 스크롤을 맨 아래로
                    messageRecyclerView.post {
                        messageRecyclerView.smoothScrollToPosition(messages.size - 1)
                    }
                }
            }.onFailure { exception ->
                Log.e("ChatRoomActivity", "메시지 로드 실패", exception)
                
                // API 실패 시 샘플 데이터 로드
                runOnUiThread {
                    loadSampleMessages()
                }
            }
        }
    }
    
    /**
     * 샘플 메시지 로드 (API 실패 시 사용)
     */
    private fun loadSampleMessages() {
        val messages = listOf(
            Message(
                messageId = 1,
                sender = "조니",
                content = "안녕하세요! 추천 친구 보고 연락드려요~",
                timestamp = "오전 10:30",
                isFromMe = false
            ),
            Message(
                messageId = 2,
                sender = "나",
                content = "안녕하세요~!^^",
                timestamp = "오전 10:32",
                isFromMe = true
            ),
            Message(
                messageId = 3,
                sender = "조니",
                content = "혹시 오늘 산책 계획 있으신가요?",
                timestamp = "오전 10:35",
                isFromMe = false
            ),
            Message(
                messageId = 4,
                sender = "나",
                content = "네! 같이 산책하실래요?!",
                timestamp = "오전 10:37",
                isFromMe = true
            ),
            Message(
                messageId = 5,
                sender = "조니",
                content = "좋아요! 그럼 6시에 연남에서 보시는 거 어떠세요?",
                timestamp = "오전 10:40",
                isFromMe = false
            ),
            Message(
                messageId = 6,
                sender = "나",
                content = "좋습니다~!",
                timestamp = "오전 10:42",
                isFromMe = true
            ),
            Message(
                messageId = 7,
                sender = "시스템",
                content = "course_share",
                timestamp = "오전 10:45",
                isFromMe = false,
                isCourseShare = true
            )
        )
        
        messageAdapter.submitList(messages)
    }
    
    private fun setupSystemUI() {
        // Status Bar 투명하게 설정
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        
        // 시스템 UI 플래그 설정
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        )
    }
    
    /**
     * 새 채팅방 생성 메시지 표시
     */
    private fun showNewChatMessage(friendName: String) {
        val welcomeMessage = Message(
            messageId = System.currentTimeMillis().toInt(),
            sender = "시스템",
            content = "${friendName}님과의 채팅방이 생성되었습니다!",
            timestamp = "방금 전",
            isSystemMessage = true
        )
        
        val currentMessages = messageAdapter.currentList.toMutableList()
        currentMessages.add(welcomeMessage)
        messageAdapter.submitList(currentMessages)
        messageRecyclerView.post { messageRecyclerView.smoothScrollToPosition(currentMessages.size - 1) }
    }
}

// 기존 ChatMessage는 새로운 Message로 대체됨 
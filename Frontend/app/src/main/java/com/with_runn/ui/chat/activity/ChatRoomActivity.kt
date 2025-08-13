package com.with_runn.ui.chat.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.with_runn.ui.chat.data.ChatReadTimeManager
import com.with_runn.ui.chat.data.ChatMessageManager
import com.with_runn.ui.chat.data.ChatRoomNameManager
import com.with_runn.ui.chat.network.WebSocketManager
import com.with_runn.ui.chat.data.UnreadMessageManager

class ChatRoomActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var chatTitle: TextView
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageAdapter: ChatMessageAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    
    private var currentParticipants = mutableListOf<String>()
    private var originalFriendName = ""
    private val chatRepository = ChatRepository()
    private lateinit var readTimeManager: ChatReadTimeManager
    private lateinit var messageManager: ChatMessageManager
    private lateinit var unreadMessageManager: UnreadMessageManager
    private var chatId = -1 // Intent에서 받아올 예정
    
    // WebSocket 관련
    private val webSocketManager = WebSocketManager.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ChatRoomActivity", "onCreate 시작")
        
        // 시스템 UI 설정
        setupSystemUI()
        
        setContentView(R.layout.activity_chat_room)
        Log.d("ChatRoomActivity", "레이아웃 설정 완료")
        
        // Intent에서 chatId 받아오기
        chatId = intent.getIntExtra("chatId", -1)
        if (chatId == -1) {
            Log.e("ChatRoomActivity", "chatId가 전달되지 않았습니다")
            finish()
            return
        }
        Log.d("ChatRoomActivity", "받은 chatId: $chatId")
        
        // 읽은 시간 관리자 초기화
        readTimeManager = ChatReadTimeManager.getInstance(this)
        messageManager = ChatMessageManager.getInstance(this)
        unreadMessageManager = UnreadMessageManager.getInstance(this)
        
        setupViews()
        setupClickListeners()
        
        // 채팅방 입장 시 마지막 읽은 시간 업데이트 및 unreadMsgCnt 리셋
        updateLastReadTime()
        resetUnreadCount()
        
        // WebSocket 연결 및 채팅방 구독
        setupWebSocket()
        
        // 새로 생성된 채팅방인지 확인
        val isNewChat = intent.getBooleanExtra("is_new_chat", false)
        if (isNewChat) {
            Log.d("ChatRoomActivity", "새로 생성된 채팅방이므로 메시지를 로드하지 않습니다")
        } else {
            // 기존 채팅방인 경우에만 메시지 로드
            loadChatMessages()
        }
        Log.d("ChatRoomActivity", "onCreate 완료")
    }

    override fun onBackPressed() {
        // 뒤로가기 시에도 떠나기 PATCH 보장
        leaveChatRoomAndFinish()
    }
    
    /**
     * WebSocket 설정
     */
    private fun setupWebSocket() {
        // 채팅방 입장은 메시지 조회 API에서 자동으로 처리됨
        // 별도의 채팅방 입장 API 호출 제거
        
        // WebSocket 연결 시도
        webSocketManager.connect(
            onConnected = {
                Log.d("ChatRoomActivity", "WebSocket 연결 성공")
                // 채팅방 구독
                webSocketManager.subscribeToChatRoom(chatId) { message ->
                    // 메시지 수신 시 UI 업데이트
                    runOnUiThread {
                        handleReceivedMessage(message)
                    }
                }
            },
            onError = { error ->
                Log.e("ChatRoomActivity", "WebSocket 연결 실패: $error")
                runOnUiThread {
                    Toast.makeText(this, "실시간 채팅 연결에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    
    /**
     * 수신된 메시지 처리 (서버 중심)
     */
    private fun handleReceivedMessage(message: Message) {
        // 내가 보낸 메시지인 경우, 이미 UI에 표시되어 있으므로 중복 처리하지 않음
        if (message.isFromMe) {
            Log.d("ChatRoomActivity", "내가 보낸 메시지이므로 UI 업데이트 생략: ${message.content}")
            return
        }
        
        val currentMessages = messageAdapter.currentList
        val updatedMessages = currentMessages.toMutableList()
        updatedMessages.add(message)
        messageAdapter.submitList(updatedMessages)
        
        // 수신된 메시지를 로컬에 임시 저장 (캐시용)
        messageManager.addMessage(chatId, message)
        
        // 새 메시지 수신 시 unreadMsgCnt 증가
        unreadMessageManager.incrementUnreadCount(chatId)
        Log.d("ChatRoomActivity", "새 메시지 수신으로 unreadMsgCnt 증가: ${message.content}")
        
        // 스크롤을 맨 아래로
        messageRecyclerView.post {
            messageRecyclerView.smoothScrollToPosition(updatedMessages.size - 1)
        }
        
        Log.d("ChatRoomActivity", "메시지 수신: ${message.content}")
    }
    
    private fun setupViews() {
        Log.d("ChatRoomActivity", "setupViews 시작")
        backButton = findViewById(R.id.back_button)
        menuButton = findViewById(R.id.menu_button)
        chatTitle = findViewById(R.id.chat_title)
        messageRecyclerView = findViewById(R.id.message_recycler_view)
        messageInput = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)
        
        // Intent에서 친구 정보 가져오기
        originalFriendName = intent.getStringExtra("friend_name") ?: "조니"
        Log.d("ChatRoomActivity", "친구 이름: $originalFriendName")
        currentParticipants.add(originalFriendName)
        
        // 로컬에 저장된 채팅방 이름이 있으면 우선 사용
        val chatRoomNameManager = ChatRoomNameManager.getInstance(this)
        val localRoomName = chatRoomNameManager.getChatRoomName(chatId)
        if (localRoomName != null) {
            Log.d("ChatRoomActivity", "로컬에 저장된 채팅방 이름 사용: $localRoomName")
            chatTitle.text = localRoomName
        } else {
            Log.d("ChatRoomActivity", "기본 친구 이름 사용: $originalFriendName")
            chatTitle.text = originalFriendName
        }
        
        val linearLayoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        messageRecyclerView.layoutManager = linearLayoutManager
        messageAdapter = ChatMessageAdapter()
        messageAdapter.setOnProfileImageClickListener { sender ->
            showFriendProfileDialog(sender)
        }
        messageRecyclerView.adapter = messageAdapter

        // 무한 스크롤(이전 메시지 로드)
        messageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) { // 맨 위 도달
                    loadMorePreviousMessages()
                }
            }
        })
        
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
            // 채팅방 나가기 API 호출 후 결과에 따라 처리
            leaveChatRoomAndFinish()
        }
        
        menuButton.setOnClickListener {
            showMenuPopup()
        }
        
        // 전송 버튼 클릭 리스너
        sendButton.setOnClickListener {
            sendMessage()
        }
        
        // 엔터키로도 전송 가능하도록 설정
        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }
    
    /**
     * 채팅방 나가기 후 Activity 종료
     */
    private fun leaveChatRoomAndFinish() {
        chatRepository.leaveChatRoom(chatId) { result ->
            result.fold(
                onSuccess = {
                    Log.d("ChatRoomActivity", "채팅방 나가기 성공")
                    // 성공 시 Activity 종료
                    runOnUiThread {
                        finish()
                    }
                },
                onFailure = { exception ->
                    Log.e("ChatRoomActivity", "채팅방 나가기 실패", exception)
                    
                    // 사용자에게 에러 메시지 표시 (Activity는 종료하지 않음)
                    runOnUiThread {
                        val errorMessage = when {
                            exception.message?.contains("서버 오류") == true -> {
                                exception.message ?: "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                            }
                            exception.message?.contains("네트워크") == true -> {
                                "네트워크 연결을 확인해주세요."
                            }
                            else -> {
                                "채팅방을 나가는데 실패했습니다. 잠시 후 다시 시도해주세요."
                            }
                        }
                        
                        Toast.makeText(this@ChatRoomActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
    
    /**
     * 채팅방 나가기 (기존 메서드 - 다른 곳에서 호출할 때 사용)
     */
    private fun leaveChatRoom() {
        chatRepository.leaveChatRoom(chatId) { result ->
            result.fold(
                onSuccess = {
                    Log.d("ChatRoomActivity", "채팅방 나가기 성공")
                    // 성공 시 별도 처리 없이 Activity 종료
                },
                onFailure = { exception ->
                    Log.e("ChatRoomActivity", "채팅방 나가기 실패", exception)
                    
                    // 사용자에게 에러 메시지 표시
                    runOnUiThread {
                        val errorMessage = when {
                            exception.message?.contains("서버 오류") == true -> {
                                exception.message ?: "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                            }
                            exception.message?.contains("네트워크") == true -> {
                                "네트워크 연결을 확인해주세요."
                            }
                            else -> {
                                "채팅방을 나가는데 실패했습니다. 잠시 후 다시 시도해주세요."
                            }
                        }
                        
                        Toast.makeText(this@ChatRoomActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            )
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
        bottomSheet.setChatId(chatId) // 실제 chatId 전달
        bottomSheet.setOnParticipantAddedListener { newParticipant ->
            addParticipantToChat(newParticipant)
        }
        bottomSheet.show(supportFragmentManager, "AddParticipantBottomSheet")
    }
    
    private fun addParticipantToChat(newParticipant: String) {
        if (!currentParticipants.contains(newParticipant)) {
            currentParticipants.add(newParticipant)
        }
    }
    
    private fun showChatRoomNameSettingDialog() {
        val dialog = ChatRoomNameSettingDialogFragment()
        dialog.setChatId(chatId) // 실제 chatId 전달
        dialog.setOnNameSetListener { roomName ->
            // 채팅방 이름 설정 처리 (null 체크 추가)
            if (roomName != null) {
                chatTitle.text = roomName
                
                // 로컬에 채팅방 이름 저장
                val chatRoomNameManager = ChatRoomNameManager.getInstance(this@ChatRoomActivity)
                chatRoomNameManager.saveChatRoomName(chatId, roomName)
                Log.d("ChatRoomActivity", "채팅방 이름 로컬 저장: $roomName")
            }
        }
        dialog.show(supportFragmentManager, "ChatRoomNameSettingDialog")
    }
    
    private fun showFriendProfileDialog(friendName: String) {
        val dialog = FriendProfileDialogFragment.newInstance(
            friendName = friendName,
            personalityTag = "활발한",
            personalityTags = arrayListOf("활발한", "친근한", "사교적인"),
            imageResId = R.drawable.jonny,
            userId = 0
        )
        dialog.show(supportFragmentManager, "FriendProfileDialog")
    }
    
    /**
     * 채팅 메시지를 로드 (서버 우선, 로컬은 임시 캐시로만 사용)
     */
    private fun loadChatMessages() {
        Log.d("ChatRoomActivity", "메시지 로드 시작")
        
        // 1. 먼저 로컬에 저장된 메시지 로드 (임시 표시용)
        val localMessages = messageManager.loadMessages(chatId)
        if (localMessages.isNotEmpty()) {
            Log.d("ChatRoomActivity", "임시 로컬 메시지 로드: ${localMessages.size}개")
            messageAdapter.submitList(localMessages)
            if (localMessages.isNotEmpty()) {
                messageRecyclerView.post {
                    messageRecyclerView.smoothScrollToPosition(localMessages.size - 1)
                }
            }
        }
        
        // 2. API에서 메시지 로드 (서버 데이터 우선)
        chatRepository.getChatMessages(chatId) { result ->
            result.fold(
                onSuccess = { apiMessages ->
                    Log.d("ChatRoomActivity", "✅ API 메시지 로드 성공: ${apiMessages.size}개")
                    
                    // API 메시지가 있으면 서버 데이터로 교체
                    if (apiMessages.isNotEmpty()) {
                        runOnUiThread {
                            messageAdapter.submitList(apiMessages)
                            messageRecyclerView.post {
                                messageRecyclerView.smoothScrollToPosition(apiMessages.size - 1)
                            }
                            Log.d("ChatRoomActivity", "서버 메시지로 UI 업데이트 완료")
                        }
                        
                        // 로컬 메시지 초기화 (서버 데이터로 교체)
                        messageManager.clearMessages(chatId)
                        // 서버 메시지를 로컬에 저장 (임시 캐시용)
                        apiMessages.forEach { message -> messageManager.addMessage(chatId, message) }
                    } else {
                        Log.d("ChatRoomActivity", "서버에 메시지가 없음")
                        // 서버에 메시지가 없고 로컬에도 없으면 샘플 데이터 로드
                        if (localMessages.isEmpty()) {
                            runOnUiThread {
                                loadSampleMessages()
                            }
                        }
                    }
                },
                onFailure = { exception ->
                    Log.e("ChatRoomActivity", "❌ API 메시지 로드 실패", exception)
                    
                    // 사용자에게 에러 메시지 표시 (선택적)
                    runOnUiThread {
                        val errorMessage = when {
                            exception.message?.contains("서버 오류") == true -> {
                                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                            }
                            exception.message?.contains("서버 연결이 끊어졌습니다") == true -> {
                                "서버 연결이 끊어졌습니다. 잠시 후 다시 시도해주세요."
                            }
                            exception.message?.contains("네트워크") == true -> {
                                "네트워크 연결을 확인해주세요."
                            }
                            else -> {
                                "메시지를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요."
                            }
                        }
                        
                        Toast.makeText(this@ChatRoomActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    
                    // API 실패 시 로컬 메시지가 있으면 그대로 사용, 없으면 샘플 데이터 로드
                    if (localMessages.isEmpty()) {
                        runOnUiThread {
                            loadSampleMessages()
                        }
                    }
                }
            )
        }
    }

    private var isLoadingMore = false
    private fun loadMorePreviousMessages() {
        if (isLoadingMore) return
        val currentList = messageAdapter.currentList
        if (currentList.isEmpty()) return
        val oldest = currentList.firstOrNull()
        // MessageDto.messageId가 toMessage에서 userId로 매핑되는 점을 고려, 서버 messageId 사용 필요
        val cursorId = messageManager.loadMessages(chatId).minByOrNull { it.messageId }?.messageId ?: return

        isLoadingMore = true
        chatRepository.getChatMessagesBefore(chatId, cursorId) { result ->
            runOnUiThread {
                result.fold(
                    onSuccess = { moreMessages ->
                        if (moreMessages.isNotEmpty()) {
                            val merged = (moreMessages + messageAdapter.currentList).distinctBy { it.messageId }
                            messageAdapter.submitList(merged)
                            // 유지 스크롤 위치 보정
                            messageRecyclerView.post { messageRecyclerView.scrollToPosition(moreMessages.size) }
                        } else {
                            // 더 없음
                        }
                        isLoadingMore = false
                    },
                    onFailure = {
                        isLoadingMore = false
                    }
                )
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
    
    /**
     * 마지막 읽은 시간 업데이트
     */
    private fun updateLastReadTime() {
        readTimeManager.setLastReadTime(chatId)
        Log.d("ChatRoomActivity", "채팅방 $chatId 마지막 읽은 시간 업데이트")
    }
    
    /**
     * unreadMsgCnt 리셋
     */
    private fun resetUnreadCount() {
        unreadMessageManager.resetUnreadCount(chatId)
        Log.d("ChatRoomActivity", "채팅방 $chatId unreadMsgCnt 리셋")
    }
    
    override fun onResume() {
        super.onResume()
        // 채팅방으로 돌아올 때마다 마지막 읽은 시간 업데이트
        updateLastReadTime()
    }
    
    

    /**
     * 메시지 전송 (서버 중심)
     */
    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isEmpty()) {
            return
        }
        
        // 입력 필드 초기화
        messageInput.text.clear()
        
        // 새 메시지 생성 (임시 ID 사용) - 즉시 현재 시간 포맷으로 표시
        val nowFormatted = java.text.SimpleDateFormat("a h:mm", java.util.Locale.KOREAN)
            .format(java.util.Date())
        val newMessage = Message(
            messageId = System.currentTimeMillis().toInt(),
            sender = "나",
            content = messageText,
            timestamp = nowFormatted,
            isFromMe = true
        )
        
        // 메시지 목록에 추가 (즉시 UI 업데이트)
        val currentMessages = messageAdapter.currentList.toMutableList()
        currentMessages.add(newMessage)
        messageAdapter.submitList(currentMessages)
        
        // 스크롤을 맨 아래로
        messageRecyclerView.post {
            messageRecyclerView.smoothScrollToPosition(currentMessages.size - 1)
        }
        
        // WebSocket으로 메시지 전송 (서버 중심)
        if (webSocketManager.isConnected()) {
            webSocketManager.sendMessage(chatId, messageText)
            Log.d("ChatRoomActivity", "✅ WebSocket을 통해 메시지 전송: $messageText")
            
            // 메시지 전송 시에는 로컬 저장하지 않음 (WebSocket 수신 시에 저장됨)
            // messageManager.addMessage(chatId, newMessage)
        } else {
            Log.w("ChatRoomActivity", "⚠️ WebSocket이 연결되지 않아 메시지를 전송할 수 없습니다")
            runOnUiThread {
                Toast.makeText(this, "실시간 연결이 끊어져 메시지를 전송할 수 없습니다", Toast.LENGTH_SHORT).show()
            }
            
            // 전송 실패 시 UI에서 메시지 제거
            val updatedMessages = currentMessages.toMutableList()
            updatedMessages.removeAt(updatedMessages.size - 1)
            messageAdapter.submitList(updatedMessages)
        }
        
        Log.d("ChatRoomActivity", "메시지 전송 완료: $messageText")
    }
} 
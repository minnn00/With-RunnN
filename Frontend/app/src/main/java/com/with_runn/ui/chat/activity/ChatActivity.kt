package com.with_runn.ui.chat.activity

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
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
import android.view.WindowInsetsController
import android.widget.FrameLayout
import com.with_runn.R
import com.with_runn.ui.chat.model.ChatRoom
import com.with_runn.ui.chat.model.dto.ChatRoomDto
import com.with_runn.ui.chat.model.dto.ChatListResponse
import com.with_runn.ui.chat.model.mapper.ChatRoomMapper.toChatRoom
import com.with_runn.ui.chat.adapter.ChatAdapter
import com.with_runn.ui.chat.repository.ChatRepository
import com.with_runn.ui.chat.network.RetrofitClient
import com.with_runn.ui.chat.data.UnreadMessageManager
import com.with_runn.ui.chat.data.ChatRoomNameManager
import com.with_runn.ui.chat.network.WebSocketManager
import com.with_runn.ui.chat.model.dto.MessageListResponse
import java.text.SimpleDateFormat
import java.util.Locale


class ChatActivity : AppCompatActivity() {
    
    private lateinit var chatAdapter: ChatAdapter
    private var chatRooms: List<ChatRoom> = listOf()
    private var currentSwipedPosition = -1
    private val chatRepository = ChatRepository()
    private lateinit var unreadMessageManager: UnreadMessageManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 시스템 UI 설정
        setupSystemUI()
        
        setContentView(R.layout.activity_chat)
        
        // UnreadMessageManager 초기화
        unreadMessageManager = UnreadMessageManager.getInstance(this)
        
        // 뒤로가기 버튼 설정
        setupBackButton()
        
        // RecyclerView 설정
        setupRecyclerView()
        
        // API로 채팅방 목록 로드
        loadChatRoomsFromApi()
        
        // 스와이프 삭제 기능 설정
        setupSwipeToDelete()
        
        // 실시간 업데이트를 위한 WebSocket 연결
        setupWebSocketForUpdates()
    }
    
    override fun onResume() {
        super.onResume()
        // 채팅방 목록의 unreadMsgCnt를 로컬 저장소에서 업데이트
        updateUnreadCountsFromLocal()
        // 로컬에 저장된 채팅방 이름을 적용
        updateChatRoomNamesFromLocal()
        // API에서 최신 채팅방 목록을 다시 가져와서 lastMessage 업데이트
        refreshChatRoomsFromApi()
    }
    
    /**
     * 로컬 저장소에서 unreadMsgCnt를 가져와서 UI 업데이트
     */
    private fun updateUnreadCountsFromLocal() {
        val updatedChatRooms = chatRooms.map { chatRoom ->
            val localUnreadCount = unreadMessageManager.getUnreadCount(chatRoom.chatId)
            chatRoom.copy(notificationCount = localUnreadCount)
        }
        
        chatRooms = updatedChatRooms
        chatAdapter.updateChatRooms(updatedChatRooms)
        Log.d("ChatActivity", "로컬 저장소에서 unreadMsgCnt 업데이트 완료")
    }
    
    /**
     * 로컬 저장소에서 채팅방 이름을 가져와서 UI 업데이트
     */
    private fun updateChatRoomNamesFromLocal() {
        val updatedChatRooms = chatRooms.map { chatRoom ->
            val localName = ChatRoomNameManager.getInstance(this).getChatRoomName(chatRoom.chatId)
            if (localName != null) {
                Log.d("ChatActivity", "onResume에서 채팅방 ${chatRoom.chatId} 로컬 이름 적용: $localName")
                chatRoom.copy(name = localName)
            } else {
                chatRoom
            }
        }
        
        chatRooms = updatedChatRooms
        chatAdapter.updateChatRooms(updatedChatRooms)
        Log.d("ChatActivity", "로컬 저장소에서 채팅방 이름 업데이트 완료")
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
            // 모든 채팅방 클릭 시 ChatRoomActivity로 이동
            val intent = Intent(this, ChatRoomActivity::class.java).apply {
                putExtra("chatId", chatRoom.chatId)
                putExtra("friend_name", chatRoom.name)
            }
            startActivity(intent)
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
                    // 왼쪽 스와이프 시 삭제 버튼 표시하고 위치 고정
                    showDeleteButton(position)
                    
                    // 아이템을 삭제 버튼 크기만큼 이동된 상태로 고정
                    val deleteButtonWidth = 80f * resources.displayMetrics.density
                    val itemView = viewHolder.itemView
                    val chatItemContainer = itemView.findViewById<View>(R.id.chat_item_container)
                    val deleteContainer = itemView.findViewById<FrameLayout>(R.id.delete_container)
                    
                    // 삭제 컨테이너를 완전히 표시
                    deleteContainer?.let { container ->
                        container.visibility = View.VISIBLE
                        val layoutParams = container.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                        layoutParams.width = deleteButtonWidth.toInt()
                        container.layoutParams = layoutParams
                        container.translationX = 0f
                    }
                    
                    chatItemContainer?.translationX = -deleteButtonWidth
                    
                } else if (direction == ItemTouchHelper.RIGHT) {
                    hideDeleteButton()
                }
            }
            
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val deleteContainer = itemView.findViewById<FrameLayout>(R.id.delete_container)
                    val deleteButton = itemView.findViewById<TextView>(R.id.delete_button)
                    val deleteBackground = itemView.findViewById<View>(R.id.delete_background)
                    val chatItemContainer = itemView.findViewById<View>(R.id.chat_item_container)
                    
                    // 삭제 버튼 크기 (카카오톡과 동일하게 80dp)
                    val deleteButtonWidth = 80f * resources.displayMetrics.density
                    
                    // 왼쪽 스와이프 (삭제 버튼 표시)
                    if (dX < 0) {
                        // 스와이프 거리를 삭제 버튼 크기로 제한 (최대 -deleteButtonWidth까지만)
                        val limitedDX = dX.coerceAtLeast(-deleteButtonWidth)
                        
                        // 스와이프 진행도 계산
                        val progress = -limitedDX / deleteButtonWidth
                        
                        // 채팅 아이템을 왼쪽으로 밀어냄
                        chatItemContainer.translationX = limitedDX
                        
                        // 스와이프 완료 시 상태 업데이트
                        if (progress >= 1f) {
                            currentSwipedPosition = viewHolder.adapterPosition
                        }
                    } else if (dX > 0) {
                        // 오른쪽 스와이프 (삭제 버튼 숨기기) - 삭제 버튼이 표시되어 있을 때만
                        if (currentSwipedPosition == viewHolder.adapterPosition) {
                            val limitedDX = dX.coerceAtMost(deleteButtonWidth)
                            val progress = limitedDX / deleteButtonWidth
                            
                            // 삭제 컨테이너를 오른쪽으로 이동시켜서 사라지게 함
                            deleteContainer?.translationX = limitedDX
                            
                            // 채팅 아이템을 원래 위치로 복원
                            chatItemContainer.translationX = -deleteButtonWidth + limitedDX
                            
                            // 완전히 복원되면 상태 리셋
                            if (progress >= 1f) {
                                deleteContainer?.visibility = View.GONE
                                deleteContainer?.translationX = 0f
                                chatItemContainer.translationX = 0f
                                currentSwipedPosition = -1
                            }
                        }
                    } else {
                        // 스와이프가 없을 때
                        if (currentSwipedPosition != viewHolder.adapterPosition) {
                            deleteContainer?.visibility = View.GONE
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
                val position = viewHolder.adapterPosition
                val itemView = viewHolder.itemView
                val chatItemContainer = itemView.findViewById<View>(R.id.chat_item_container)
                val deleteContainer = itemView.findViewById<FrameLayout>(R.id.delete_container)
                
                // 삭제 버튼이 표시된 아이템은 위치를 유지, 그렇지 않으면 리셋
                if (currentSwipedPosition == position) {
                    // 삭제 버튼이 표시된 상태이므로 위치 유지
                    val deleteButtonWidth = 80f * resources.displayMetrics.density
                    chatItemContainer?.translationX = -deleteButtonWidth
                    deleteContainer?.let { container ->
                        container.visibility = View.VISIBLE
                        // 완전히 드러난 상태로 설정
                        val layoutParams = container.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                        layoutParams.width = deleteButtonWidth.toInt()
                        container.layoutParams = layoutParams
                        container.translationX = 0f
                    }
                } else {
                    // 일반 상태로 리셋
                    val profileImage = itemView.findViewById<ImageView>(R.id.profile_image)
                    val profileImage2 = itemView.findViewById<ImageView>(R.id.profile_image_2)
                    val chatName = itemView.findViewById<TextView>(R.id.chat_name)
                    val chatTime = itemView.findViewById<TextView>(R.id.chat_time)
                    val lastMessage = itemView.findViewById<TextView>(R.id.last_message)
                    val notificationBadge = itemView.findViewById<View>(R.id.notification_badge)
                    val notificationCount = itemView.findViewById<TextView>(R.id.notification_count)
                    
                    chatItemContainer?.translationX = 0f
                    deleteContainer?.visibility = View.GONE
                    deleteContainer?.translationX = 0f
                    profileImage?.translationX = 0f
                    profileImage2?.translationX = 0f
                    chatName?.translationX = 0f
                    chatTime?.translationX = 0f
                    lastMessage?.translationX = 0f
                    notificationBadge?.translationX = 0f
                    notificationCount?.translationX = 0f
                }
                
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
            
            Log.d("ChatActivity", "=== 실제 채팅방 삭제 시작 ===")
            Log.d("ChatActivity", "삭제할 채팅방: ${deletedChatRoom.name}, chatId: ${deletedChatRoom.chatId}, 위치: $position")
            
            // 실제 API 호출로 채팅방 삭제
            val actualChatId = deletedChatRoom.chatId // 삭제할 채팅방의 실제 chatId 사용
            chatRepository.deleteChatRoom(actualChatId) { result ->
                runOnUiThread {
                    result.fold(
                        onSuccess = {
                            Log.d("ChatActivity", "✅ 실제 채팅방 삭제 성공!")
                            
                            // 삭제 성공 후 API에서 최신 채팅 목록을 다시 불러옴
                            loadChatRoomsFromApi()
                            hideDeleteButton()
                            
                            Toast.makeText(this@ChatActivity, "${deletedChatRoom.name} 채팅방이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { exception ->
                            Log.e("ChatActivity", "❌ 실제 채팅방 삭제 실패", exception)
                            Toast.makeText(this@ChatActivity, "채팅방 삭제에 실패했습니다: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }
    
    /**
     * API를 통해 채팅방 목록을 로드
     */
    private fun loadChatRoomsFromApi() {
        Log.d("ChatActivity", "API 호출 시작")
        
        // Retrofit의 enqueue() 메서드 사용
        RetrofitClient.chatApiService.getChatList().enqueue(object : retrofit2.Callback<ChatListResponse> {
            override fun onResponse(
                call: retrofit2.Call<ChatListResponse>,
                response: retrofit2.Response<ChatListResponse>
            ) {
                Log.d("ChatActivity", "API 응답 성공: ${response.code()}")
                
                if (response.isSuccessful) {
                    val chatListResponse = response.body()
                    Log.d("ChatActivity", "받은 데이터: $chatListResponse")
                    
                    if (chatListResponse != null && chatListResponse.success) {
                        val chatRoomDtos = chatListResponse.result
                        Log.d("ChatActivity", "API에서 가져온 채팅방 개수: ${chatRoomDtos.size}")
                        
                        // 🆕 각 채팅방의 lastReceivedMsg 상세 로그
                        chatRoomDtos.forEach { dto ->
                            Log.d("ChatActivity", "채팅방 ${dto.chatId}:")
                            Log.d("ChatActivity", "  lastReceivedMsg: '${dto.lastReceivedMsg}'")
                            Log.d("ChatActivity", "  lastReceivedMsg 타입: ${dto.lastReceivedMsg?.javaClass?.simpleName}")
                            Log.d("ChatActivity", "  chatName: '${dto.chatName}'")
                        }
                        
                        // DTO를 UI 모델로 변환 (마지막 메시지는 나중에 추가)
                        val chatRooms = chatRoomDtos.map { it.toChatRoom() }
                        Log.d("ChatActivity", "변환된 채팅방: $chatRooms")
                        
                        // 로컬에 저장된 채팅방 이름으로 덮어쓰기
                        val chatRoomsWithLocalNames = chatRooms.map { chatRoom ->
                            val localName = ChatRoomNameManager.getInstance(this@ChatActivity).getChatRoomName(chatRoom.chatId)
                            if (localName != null) {
                                Log.d("ChatActivity", "채팅방 ${chatRoom.chatId} 로컬 이름 사용: $localName")
                                chatRoom.copy(name = localName)
                            } else {
                                chatRoom
                            }
                        }
                        
                        // UI 업데이트 (마지막 메시지 없이 먼저 표시)
                        runOnUiThread {
                            this@ChatActivity.chatRooms = chatRoomsWithLocalNames
                            chatAdapter.updateChatRooms(chatRoomsWithLocalNames)
                        }
                        
                        // 각 채팅방의 마지막 메시지를 가져와서 업데이트
                        loadLastMessagesForChatRooms(chatRooms)
                    } else {
                        Log.e("ChatActivity", "응답이 성공하지 않음")
                        loadSampleData() // API 실패 시 샘플 데이터 로드
                    }
                } else {
                    // 400 에러인 경우 (참여 중인 채팅방이 없음) 빈 리스트 처리
                    if (response.code() == 400) {
                        Log.d("ChatActivity", "참여 중인 채팅방이 없습니다. 빈 리스트를 표시합니다.")
                        runOnUiThread {
                            this@ChatActivity.chatRooms = emptyList()
                            chatAdapter.updateChatRooms(emptyList())
                        }
                    } else {
                        Log.e("ChatActivity", "API 호출 실패: ${response.code()} - ${response.message()}")
                        loadSampleData() // API 실패 시 샘플 데이터 로드
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<ChatListResponse>, t: Throwable) {
                Log.e("ChatActivity", "API 호출 실패", t)
                loadSampleData() // 네트워크 오류 시 샘플 데이터 로드
            }
        })
    }
    
    /**
     * 각 채팅방의 마지막 메시지를 가져와서 업데이트
     */
    private fun loadLastMessagesForChatRooms(chatRooms: List<ChatRoom>) {
        chatRooms.forEach { chatRoom ->
            loadLastMessageForChatRoom(chatRoom.chatId) { lastMessage, createdAt ->
                if (lastMessage.isNotEmpty()) {
                    val formattedTime = try {
                        if (createdAt.contains("T")) {
                            val normalized = if (createdAt.contains('.')) {
                                val base = createdAt.substringBefore('.')
                                val frac = createdAt.substringAfter('.')
                                val frac6 = if (frac.length >= 6) frac.substring(0, 6) else frac.padEnd(6, '0')
                                "$base.$frac6"
                            } else createdAt
                            val date = if (normalized.contains('.')) {
                                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", java.util.Locale.getDefault()).parse(normalized)
                            } else {
                                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).parse(normalized)
                            }
                            java.text.SimpleDateFormat("a h:mm", java.util.Locale.KOREAN).format(date ?: java.util.Date())
                        } else createdAt
                    } catch (e: Exception) { "방금 전" }

                    val updatedChatRooms = this@ChatActivity.chatRooms.map { room ->
                        if (room.chatId == chatRoom.chatId) {
                            room.copy(lastMessage = lastMessage, time = formattedTime)
                        } else {
                            room
                        }
                    }

                    runOnUiThread {
                        this@ChatActivity.chatRooms = updatedChatRooms
                        chatAdapter.updateChatRooms(updatedChatRooms)
                    }
                }
            }
        }
    }
    
    /**
     * 특정 채팅방의 마지막 메시지를 가져오기
     */
    private fun loadLastMessageForChatRoom(chatId: Int, callback: (String, String) -> Unit) {
        RetrofitClient.chatApiService.getChatMessages(chatId).enqueue(object : retrofit2.Callback<MessageListResponse> {
            override fun onResponse(
                call: retrofit2.Call<MessageListResponse>,
                response: retrofit2.Response<MessageListResponse>
            ) {
                if (response.isSuccessful) {
                    val messageListResponse = response.body()
                    if (messageListResponse != null && messageListResponse.success) {
                        val messages = messageListResponse.result
                        if (messages.isNotEmpty()) {
                            // 가장 최근 메시지 (마지막 메시지) 가져오기
                            val lastMessage = messages.last()
                            Log.d("ChatActivity", "채팅방 $chatId 마지막 메시지: ${lastMessage.msg}")
                            callback(lastMessage.msg, lastMessage.createdAt)
                        } else {
                            Log.d("ChatActivity", "채팅방 $chatId 메시지가 없음")
                            callback("", "")
                        }
                    } else {
                        Log.e("ChatActivity", "메시지 조회 실패: 응답이 성공하지 않음")
                        callback("", "")
                    }
                } else {
                    // 500 에러인 경우 특별 처리
                    if (response.code() == 500) {
                        val errorBody = response.errorBody()?.string()
                        Log.w("ChatActivity", "⚠️ 서버 오류 발생: $errorBody")
                        
                        // 에러 응답에서 메시지 추출
                        val errorMessage = try {
                            if (!errorBody.isNullOrEmpty()) {
                                val gson = com.google.gson.Gson()
                                val jsonObject = gson.fromJson(errorBody, com.google.gson.JsonObject::class.java)
                                jsonObject.get("message")?.asString ?: "서버 오류가 발생했습니다."
                            } else {
                                "서버 오류가 발생했습니다."
                            }
                        } catch (e: Exception) {
                            Log.e("ChatActivity", "에러 응답 파싱 실패", e)
                            "서버 오류가 발생했습니다."
                        }
                        
                        // "Query did not return a unique result" 에러인 경우 특별 처리
                        if (errorMessage.contains("Query did not return a unique result") || 
                            errorMessage.contains("2 results were returned")) {
                            Log.w("ChatActivity", "⚠️ 데이터베이스 중복 데이터 문제: $chatId")
                            // 중복 데이터 문제는 서버 측 문제이므로 빈 메시지 반환
                            callback("", "")
                        } else {
                            Log.w("ChatActivity", "⚠️ 기타 서버 오류: $errorMessage")
                            callback("", "")
                        }
                    } else {
                        Log.e("ChatActivity", "메시지 조회 실패: ${response.code()}")
                        callback("", "")
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<MessageListResponse>, t: Throwable) {
                Log.e("ChatActivity", "메시지 조회 네트워크 실패", t)
                
                // EOFException인 경우 특별 처리
                when {
                    t is java.io.EOFException -> {
                        Log.w("ChatActivity", "서버 연결이 끊어짐: $chatId")
                    }
                    t is java.net.SocketTimeoutException -> {
                        Log.w("ChatActivity", "요청 시간 초과: $chatId")
                    }
                    t is java.net.UnknownHostException -> {
                        Log.w("ChatActivity", "서버에 연결할 수 없음: $chatId")
                    }
                    else -> {
                        Log.w("ChatActivity", "기타 네트워크 오류: $chatId")
                    }
                }
                
                callback("", "")
            }
        })
    }
    
    /**
     * 샘플 데이터 로드 (API 실패 시 사용)
     */
    private fun loadSampleData() {
        chatRooms = listOf(
            ChatRoom(
                chatId = 1,
                name = "조니",
                time = "14:20",
                lastMessage = "좋아요! 그럼 6시에 연남에서 보는 거 어떠세요?",
                notificationCount = 1,
                profileImageResId = R.drawable.jonny
            ),
            ChatRoom(
                chatId = 2,
                name = "마루",
                time = "10:00",
                lastMessage = "산책 즐거웠어요 ~ 다음에 또 같이 해요~!",
                profileImageResId = R.drawable.maru
            ),
            ChatRoom(
                chatId = 3,
                name = "이름 없는 사용자",
                time = "25.05.29",
                lastMessage = "저기요 제 개껌 돌려달라고요",
                profileImageResId = R.drawable.guri
            )
        )
        
        chatAdapter.updateChatRooms(chatRooms)
    }
    
    private fun setupSystemUI() {
        // 시스템 UI 컨트롤러 설정
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.isAppearanceLightNavigationBars = true
    }
    
    /**
     * 채팅방 삭제 기능 테스트
     */
    private fun testDeleteChatRoomFeature() {
        Log.d("ChatActivity", "=== 채팅방 삭제 기능 테스트 시작 ===")
        
        val chatId = 1
        chatRepository.deleteChatRoom(chatId) { result ->
            result.fold(
                onSuccess = {
                    Log.d("ChatActivity", "✅ 채팅방 삭제 성공!")
                },
                onFailure = { exception ->
                    Log.e("ChatActivity", "❌ 채팅방 삭제 실패", exception)
                }
            )
        }
    }
    

    
    /**
     * 채팅방 생성 기능 테스트
     */
    private fun testCreateChatRoomFeature() {
        Log.d("ChatActivity", "=== 채팅방 생성 기능 테스트 시작 ===")
        Log.d("ChatActivity", "채팅방 생성 테스트 함수가 호출되었습니다!")
        
        // 테스트용 사용자 ID (실제로는 로그인한 사용자 ID를 사용)
        val currentUserId = 1
        val targetUserId = 2
        
        Log.d("ChatActivity", "테스트 파라미터: currentUserId=$currentUserId, targetUserId=$targetUserId")
        
        chatRepository.createChatRoomAndFind(currentUserId, targetUserId, "테스트 친구") { result ->
            runOnUiThread {
                result.fold(
                    onSuccess = { chatRoom ->
                        if (chatRoom != null) {
                            Log.d("ChatActivity", "✅ 채팅방 생성 및 찾기 성공! chatId=${chatRoom.chatId}, name=${chatRoom.name}")
                            Toast.makeText(this@ChatActivity, "채팅방이 성공적으로 생성되었습니다!", Toast.LENGTH_SHORT).show()
                            
                            // 생성된 채팅방으로 바로 이동
                            navigateToChatRoom(chatRoom.chatId, "테스트 친구")
                        } else {
                            Log.d("ChatActivity", "⚠️ 생성된 채팅방을 찾을 수 없음")
                            Toast.makeText(this@ChatActivity, "채팅방이 성공적으로 생성되었습니다!", Toast.LENGTH_SHORT).show()
                            
                            // 채팅 목록 새로고침
                            loadChatRoomsFromApi()
                        }
                    },
                    onFailure = { exception ->
                        Log.e("ChatActivity", "❌ 채팅방 생성 실패", exception)
                        Toast.makeText(this@ChatActivity, "채팅방 생성에 실패했습니다: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
        
        Log.d("ChatActivity", "채팅방 생성 기능 테스트 호출 완료")
    }
    
    /**
     * 생성된 채팅방으로 이동
     */
    private fun navigateToChatRoom(chatId: Int, friendName: String) {
        val intent = Intent(this, ChatRoomActivity::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("friend_name", friendName)
            putExtra("is_new_chat", true) // 새 채팅방 플래그
        }
        startActivity(intent)
    }
    
    /**
     * 실시간 업데이트를 위한 WebSocket 연결
     */
    private fun setupWebSocketForUpdates() {
        val webSocketManager = WebSocketManager.getInstance()
        webSocketManager.connect(
            onConnected = {
                Log.d("ChatActivity", "WebSocket 연결 성공 (실시간 업데이트용)")
                // 모든 채팅방에 대해 메시지 수신 리스너 설정
                setupMessageListeners()
            },
            onError = { error ->
                Log.e("ChatActivity", "WebSocket 연결 실패 (실시간 업데이트용): $error")
            }
        )
    }
    
    /**
     * 메시지 수신 리스너 설정
     */
    private fun setupMessageListeners() {
        val webSocketManager = WebSocketManager.getInstance()
        
        // 현재 채팅방 목록의 모든 채팅방에 대해 메시지 수신 리스너 설정
        chatRooms.forEach { chatRoom ->
            webSocketManager.subscribeToChatRoom(chatRoom.chatId) { message ->
                // 새 메시지 수신 시 unreadMsgCnt 증가 (내가 보낸 메시지가 아닌 경우만)
                if (!message.isFromMe) {
                    runOnUiThread {
                        updateUnreadCount(chatRoom.chatId, +1)
                    }
                }
                
                // 🆕 모든 메시지에 대해 lastMessage와 time 업데이트
                runOnUiThread {
                    updateChatRoomLastMessage(chatRoom.chatId, message.content, message.timestamp)
                }
            }
        }
    }
    
    /**
     * 특정 채팅방의 unreadMsgCnt 업데이트
     */
    private fun updateUnreadCount(chatId: Int, increment: Int) {
        val currentCount = unreadMessageManager.getUnreadCount(chatId)
        val newCount = maxOf(0, currentCount + increment)
        unreadMessageManager.setUnreadCount(chatId, newCount)
        
        // UI 업데이트
        updateChatRoomUnreadCount(chatId, newCount)
        Log.d("ChatActivity", "채팅방 $chatId unreadMsgCnt 업데이트: $currentCount -> $newCount")
    }
    
    /**
     * 채팅방 리스트의 특정 채팅방 unreadMsgCnt 업데이트
     */
    private fun updateChatRoomUnreadCount(chatId: Int, newCount: Int) {
        val updatedChatRooms = chatRooms.map { chatRoom ->
            if (chatRoom.chatId == chatId) {
                chatRoom.copy(notificationCount = newCount)
            } else {
                chatRoom
            }
        }
        
        chatRooms = updatedChatRooms
        chatAdapter.updateChatRooms(updatedChatRooms)
    }

    /**
     * 🆕 채팅방 리스트의 특정 채팅방 lastMessage와 time 업데이트
     */
    private fun updateChatRoomLastMessage(chatId: Int, messageContent: String, timestamp: String) {
        val updatedChatRooms = chatRooms.map { chatRoom ->
            if (chatRoom.chatId == chatId) {
                chatRoom.copy(
                    lastMessage = messageContent,
                    time = formatTimestampForChatList(timestamp)
                )
            } else {
                chatRoom
            }
        }
        
        chatRooms = updatedChatRooms
        chatAdapter.updateChatRooms(updatedChatRooms)
        Log.d("ChatActivity", "채팅방 $chatId lastMessage 업데이트: '$messageContent'")
    }

    /**
     * 🆕 채팅 목록용 타임스탬프 포맷팅
     */
    private fun formatTimestampForChatList(timestamp: String): String {
        return try {
            // "방금 전"인 경우 그대로 반환
            if (timestamp == "방금 전") return timestamp
            
            // ISO 8601 형식 (2025-08-10T12:05:48.832766145) 파싱
            if (timestamp.contains("T")) {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(timestamp)
                if (date != null) {
                    val outputFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)
                    outputFormat.format(date)
                } else {
                    "방금 전"
                }
            } else {
                // 다른 형식인 경우 그대로 반환
                timestamp
            }
        } catch (e: Exception) {
            Log.e("ChatActivity", "타임스탬프 포맷팅 실패: $timestamp", e)
            "방금 전"
        }
    }

    /**
     * API에서 최신 채팅방 목록을 다시 가져와서 lastMessage 업데이트
     */
    private fun refreshChatRoomsFromApi() {
        Log.d("ChatActivity", "API에서 최신 채팅방 목록을 다시 가져오는 중...")
        loadChatRoomsFromApi()
    }
} 
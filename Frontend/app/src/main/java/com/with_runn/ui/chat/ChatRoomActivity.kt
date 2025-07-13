package com.with_runn.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.R
import com.with_runn.data.ChatRoom
import com.with_runn.data.Message
import com.with_runn.data.MessageType
import com.with_runn.databinding.ActivityChatRoomBinding
import com.with_runn.data.Friend
import com.with_runn.data.Gender

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private var chatRoom: ChatRoom? = null
    private var messages: MutableList<Message> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        getChatRoomFromIntent()
        setupViews()
        setupRecyclerView()
        setupClickListeners()
        loadMessages()
    }

    private fun getChatRoomFromIntent() {
        chatRoom = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("chat_room", ChatRoom::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("chat_room")
        }
    }

    private fun setupViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        // 채팅방 이름 설정
        chatRoom?.let { room ->
            binding.toolbar.title = room.name
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chatroom, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_menu -> {
                showChatMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter(object : MessagesAdapter.OnProfileClickListener {
            override fun onProfileClicked(userId: String) {
                showFriendProfile(userId)
            }
        })
        
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatRoomActivity).apply {
                stackFromEnd = true // 메시지를 하단부터 표시
            }
            adapter = messagesAdapter
        }
    }

    private fun setupClickListeners() {
        // 뒤로가기 버튼
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // 전송 버튼
        binding.fabSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun showChatMenu() {
        val popupMenu = PopupMenu(this, binding.toolbar)
        popupMenu.menuInflater.inflate(R.menu.menu_chat_room_options, popupMenu.menu)
        
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_participant -> {
                    showAddParticipantBottomSheet()
                    true
                }
                R.id.action_set_chat_name -> {
                    showSetChatNameDialog()
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }

    private fun loadMessages() {
        // 임시 메시지 데이터
        messages = createSampleMessages().toMutableList()
        messagesAdapter.submitList(messages)
        
        // 최신 메시지로 스크롤
        if (messages.isNotEmpty()) {
            binding.rvMessages.scrollToPosition(messages.size - 1)
        }
    }

    private fun createSampleMessages(): List<Message> {
        val currentTime = System.currentTimeMillis()
        
        return listOf(
            Message(
                id = "date1",
                type = MessageType.DATE_SEPARATOR,
                content = "",
                timestamp = currentTime - 86400000, // 1일 전
                timeString = "",
                dateString = "2024년 1월 15일"
            ),
            Message(
                id = "msg1",
                type = MessageType.RECEIVED,
                content = "안녕하세요! 오늘 산책 어떠세요?",
                senderId = "friend1",
                senderName = chatRoom?.name ?: "친구",
                senderProfileUrl = chatRoom?.profileImageUrl,
                timestamp = currentTime - 3600000, // 1시간 전
                timeString = "오후 1:30"
            ),
            Message(
                id = "msg2",
                type = MessageType.SENT,
                content = "좋아요! 어디로 갈까요?",
                senderId = "me",
                timestamp = currentTime - 3300000, // 55분 전
                timeString = "오후 1:35"
            ),
            Message(
                id = "system1",
                type = MessageType.SYSTEM_MESSAGE,
                content = "산책 코스를 공유하였습니다",
                timestamp = currentTime - 3000000, // 50분 전
                timeString = ""
            ),
            Message(
                id = "msg3",
                type = MessageType.RECEIVED,
                content = "감사합니다! 정말 도움이 됐어요",
                senderId = "friend1",
                senderName = chatRoom?.name ?: "친구",
                senderProfileUrl = chatRoom?.profileImageUrl,
                timestamp = currentTime - 1800000, // 30분 전
                timeString = "오후 2:00"
            ),
            Message(
                id = "msg4",
                type = MessageType.SENT,
                content = "네! 좋은 하루 되세요~",
                senderId = "me",
                timestamp = currentTime - 1200000, // 20분 전
                timeString = "오후 2:10"
            )
        )
    }

    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()
        if (messageText.isEmpty()) return
        
        val newMessage = Message(
            id = "msg_${System.currentTimeMillis()}",
            type = MessageType.SENT,
            content = messageText,
            senderId = "me",
            timestamp = System.currentTimeMillis(),
            timeString = "지금"
        )
        
        messages.add(newMessage)
        messagesAdapter.submitList(messages.toList()) // 새로운 리스트로 제출
        
        // 메시지 입력창 초기화
        binding.etMessage.text.clear()
        
        // 최신 메시지로 스크롤
        binding.rvMessages.scrollToPosition(messages.size - 1)
        
        Log.d("ChatRoom", "메시지 전송: $messageText")
    }

    private fun showAddParticipantBottomSheet() {
        Log.d("ChatRoom", "대화 상대 추가")
        val bottomSheet = AddParticipantBottomSheet.newInstance()
        bottomSheet.show(supportFragmentManager, "AddParticipantBottomSheet")
    }

    private fun showSetChatNameDialog() {
        Log.d("ChatRoom", "채팅방 이름 설정")
        val dialog = SetChatNameDialogFragment.newInstance()
        dialog.setOnChatNameChangedListener { newName ->
            // 채팅방 이름 변경
            binding.toolbar.title = newName
            // TODO: 서버에 채팅방 이름 변경 요청 전송
        }
        dialog.show(supportFragmentManager, "SetChatNameDialog")
    }

    private fun showFriendProfile(userId: String) {
        // 실제로는 API에서 사용자 정보를 가져와야 하지만, 임시로 더미 데이터 사용
        val friend = Friend(
            id = "1",
            name = "조니",
            imageUrl = "",
            tags = listOf("친근함", "활발함"),
            bio = "안녕하세요! 조니입니다.",
            age = "3살",
            ageInMonths = 36,
            breed = "골든리트리버",
            category = "대형견",
            gender = Gender.MALE,
            personality = listOf("활발함", "친근함", "사교적"),
            walkingStyle = listOf("장거리 산책", "달리기", "공놀이")
        )
        val dialog = com.with_runn.ui.friends.FriendDetailDialogFragment.newInstance(friend)
        dialog.show(supportFragmentManager, "FriendProfile")
    }

    private fun createDummyFriendFromUserId(userId: String): com.with_runn.data.Friend {
        // 실제로는 서버에서 사용자 정보를 조회해야 함
        return com.with_runn.data.Friend(
            id = userId,
            name = chatRoom?.name ?: "친구",
            imageUrl = chatRoom?.profileImageUrl ?: "",
            tags = listOf("친화적", "활발함"),
            bio = "안녕하세요! 함께 산책하며 즐거운 시간을 보내요.",
            age = "2년 3개월",
            ageInMonths = 27,
            breed = "골든리트리버",
            category = "대형견",
            gender = com.with_runn.data.Gender.MALE,
            personality = listOf("친화적", "활발함", "사교적"),
            walkingStyle = listOf("장거리 산책", "공놀이"),
            isFollowing = false
        )
    }
} 
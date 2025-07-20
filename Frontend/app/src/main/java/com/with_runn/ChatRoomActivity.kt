package com.with_runn

import android.os.Bundle
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

class ChatRoomActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var chatTitle: TextView
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageAdapter: ChatMessageAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        
        setupViews()
        setupClickListeners()
        loadChatMessages()
    }
    
    private fun setupViews() {
        backButton = findViewById(R.id.back_button)
        menuButton = findViewById(R.id.menu_button)
        chatTitle = findViewById(R.id.chat_title)
        messageRecyclerView = findViewById(R.id.message_recycler_view)
        
        chatTitle.text = "조니"
        
        messageRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        messageAdapter = ChatMessageAdapter()
        messageRecyclerView.adapter = messageAdapter
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
            Toast.makeText(this, "채팅방 이름 설정", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }

    private fun showAddParticipantBottomSheet() {
        val bottomSheet = AddParticipantBottomSheet()
        bottomSheet.show(supportFragmentManager, "AddParticipantBottomSheet")
    }
    
    private fun loadChatMessages() {
        val messages = listOf(
            ChatMessage(
                sender = "조니",
                content = "안녕하세요! 추천 친구 보고 연락드려요~",
                isFromMe = false,
                timestamp = "오전 10:30"
            ),
            ChatMessage(
                sender = "나",
                content = "안녕하세요~!^^",
                isFromMe = true,
                timestamp = "오전 10:32"
            ),
            ChatMessage(
                sender = "조니",
                content = "혹시 오늘 산책 계획 있으신가요?",
                isFromMe = false,
                timestamp = "오전 10:35"
            ),
            ChatMessage(
                sender = "나",
                content = "네! 같이 산책하실래요?!",
                isFromMe = true,
                timestamp = "오전 10:37"
            ),
            ChatMessage(
                sender = "조니",
                content = "좋아요! 그럼 6시에 연남에서 보시는 거 어떠세요?",
                isFromMe = false,
                timestamp = "오전 10:40"
            ),
            ChatMessage(
                sender = "나",
                content = "좋습니다~!",
                isFromMe = true,
                timestamp = "오전 10:42"
            ),
            ChatMessage(
                sender = "시스템",
                content = "course_share",
                isFromMe = false,
                timestamp = "오전 10:45",
                isCourseShare = true
            )
        )
        
        messageAdapter.submitList(messages)
    }
}

data class ChatMessage(
    val sender: String,
    val content: String,
    val isFromMe: Boolean,
    val timestamp: String,
    val isCourseShare: Boolean = false
) 
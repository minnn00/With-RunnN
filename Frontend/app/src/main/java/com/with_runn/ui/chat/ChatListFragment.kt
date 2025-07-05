package com.with_runn.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.R
import com.with_runn.data.ChatRoom
import com.with_runn.databinding.FragmentChatListBinding

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatListAdapter: ChatListAdapter
    private var chatRooms: List<ChatRoom> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupRecyclerView()
        setupClickListeners()
        loadChatRooms()
    }

    private fun setupViews() {
        // 필요한 초기 설정
    }

    private fun setupRecyclerView() {
        chatListAdapter = ChatListAdapter(
            onItemClick = { chatRoom ->
                openChatRoom(chatRoom)
            },
            onItemSwipe = { chatRoom ->
                deleteChatRoom(chatRoom)
            }
        )

        binding.rvChatList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatListAdapter
            
            // 스와이프 기능은 나중에 ItemTouchHelper로 구현 예정
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadChatRooms() {
        // 임시 채팅방 데이터
        chatRooms = createSampleChatRooms()
        chatListAdapter.submitList(chatRooms)
        updateEmptyState()
    }

    private fun createSampleChatRooms(): List<ChatRoom> {
        return listOf(
            ChatRoom(
                id = "chat1",
                name = "마루",
                profileImageUrl = "https://example.com/image1.jpg",
                lastMessage = "안녕하세요! 오늘 산책 어떠세요?",
                lastMessageTime = "오후 2:30",
                unreadCount = 3
            ),
            ChatRoom(
                id = "chat2",
                name = "보리",
                profileImageUrl = "https://example.com/image2.jpg",
                lastMessage = "네! 좋은 하루 되세요~",
                lastMessageTime = "오전 11:15",
                unreadCount = 0
            ),
            ChatRoom(
                id = "chat3",
                name = "초코",
                profileImageUrl = "https://example.com/image3.jpg",
                lastMessage = "산책 코스를 공유하였습니다",
                lastMessageTime = "어제",
                unreadCount = 1
            ),
            ChatRoom(
                id = "chat4",
                name = "루비",
                profileImageUrl = "https://example.com/image4.jpg",
                lastMessage = "감사합니다! 정말 도움이 됐어요",
                lastMessageTime = "2일 전",
                unreadCount = 0
            )
        )
    }

    private fun openChatRoom(chatRoom: ChatRoom) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
            putExtra("chat_room", chatRoom)
        }
        startActivity(intent)
    }

    private fun deleteChatRoom(chatRoom: ChatRoom) {
        // 채팅방 삭제 로직
        val updatedList = chatRooms.toMutableList()
        updatedList.remove(chatRoom)
        chatRooms = updatedList
        chatListAdapter.submitList(chatRooms)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (chatRooms.isEmpty()) {
            binding.llEmptyState.visibility = View.VISIBLE
            binding.rvChatList.visibility = View.GONE
        } else {
            binding.llEmptyState.visibility = View.GONE
            binding.rvChatList.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
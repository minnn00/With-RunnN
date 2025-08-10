package com.with_runn.ui.friend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R
import com.with_runn.ui.friend.adapter.RecommendedFriendAdapter
import com.with_runn.ui.friend.viewmodel.RecommendedFriendViewModel
import com.with_runn.ui.chat.activity.ChatRoomActivity
import com.with_runn.ui.chat.repository.ChatRepository

class RecommendedFriendsFragment : Fragment() {
    private lateinit var recommendedFriendAdapter: RecommendedFriendAdapter
    private lateinit var viewModel: RecommendedFriendViewModel
    private val chatRepository = ChatRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommended_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        
        // 기본값으로 추천 친구 조회 (provinceId = 1, 서울)
        loadRecommendedFriends()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[RecommendedFriendViewModel::class.java]
    }

    private fun setupRecyclerView() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recommendedFriendsRecyclerView)
        recommendedFriendAdapter = RecommendedFriendAdapter { friend ->
            // 친구 클릭 시 처리
            showFriendProfileDialog(friend)
        }
        
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendedFriendAdapter
        }
    }

    private fun setupObservers() {
        viewModel.recommendedFriends.observe(viewLifecycleOwner) { friends ->
            if (friends.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                recommendedFriendAdapter.updateFriends(friends)
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoadingState()
            } else {
                hideLoadingState()
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun showEmptyState() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recommendedFriendsRecyclerView)
        val emptyTextView = view?.findViewById<TextView>(R.id.emptyStateTextView)
        
        recyclerView?.visibility = View.GONE
        emptyTextView?.visibility = View.VISIBLE
        emptyTextView?.text = "현재 추천할 친구가 없습니다.\n다른 지역을 선택하거나 나중에 다시 시도해보세요."
    }

    private fun hideEmptyState() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recommendedFriendsRecyclerView)
        val emptyTextView = view?.findViewById<TextView>(R.id.emptyStateTextView)
        
        recyclerView?.visibility = View.VISIBLE
        emptyTextView?.visibility = View.GONE
    }

    private fun showLoadingState() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recommendedFriendsRecyclerView)
        val loadingTextView = view?.findViewById<TextView>(R.id.loadingTextView)
        
        recyclerView?.visibility = View.GONE
        loadingTextView?.visibility = View.VISIBLE
    }

    private fun hideLoadingState() {
        val loadingTextView = view?.findViewById<TextView>(R.id.loadingTextView)
        loadingTextView?.visibility = View.GONE
    }

    private fun loadRecommendedFriends(provinceId: Int = 1, cityId: Int? = null, townId: Int? = null) {
        viewModel.loadRecommendedFriends(provinceId, cityId, townId)
    }

    private fun showFriendProfileDialog(friend: com.with_runn.ui.friend.model.dto.RecommendedFriendResponse) {
        val dialogFragment = FriendProfileDialogFragment.newInstance(
            friendName = friend.userName ?: "",
            personalityTag = friend.style?.firstOrNull() ?: "",
            personalityTags = ArrayList((friend.style ?: emptyList()) + (friend.characters ?: emptyList())),
            imageResId = R.drawable.default_profile,
            userId = friend.userId
        )

        dialogFragment.setOnMessageButtonClickListener {
            createChatRoomWithFriend(friend)
        }

        dialogFragment.show(childFragmentManager, "FriendProfileDialog")
    }

    private fun createChatRoomWithFriend(friend: com.with_runn.ui.friend.model.dto.RecommendedFriendResponse) {
        // 로딩 표시
        val userName = friend.userName ?: "친구"
        Toast.makeText(requireContext(), "${userName}님과 채팅방을 생성 중입니다...", Toast.LENGTH_SHORT).show()
        
        // 실제 로그인된 사용자 ID (백엔드 개발자 요청)
        val currentUserId = 10
        
        chatRepository.createChatRoomAndFind(currentUserId, friend.userId, userName) { result ->
            requireActivity().runOnUiThread {
                result.fold(
                    onSuccess = { chatRoom ->
                        if (chatRoom != null) {
                            Toast.makeText(requireContext(), "${userName}님과의 채팅방이 생성되었습니다!", Toast.LENGTH_SHORT).show()
                            navigateToChatRoom(chatRoom.chatId, userName)
                        } else {
                            Toast.makeText(requireContext(), "${userName}님과의 채팅방이 생성되었습니다!", Toast.LENGTH_SHORT).show()
                            navigateToChatList()
                        }
                    },
                    onFailure = { exception ->
                        val errorMessage = when {
                            exception.message?.contains("서버 오류") == true -> {
                                "서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요! 😅"
                            }
                            exception.message?.contains("네트워크") == true -> {
                                "네트워크 연결을 확인해주세요! 📶"
                            }
                            else -> {
                                "채팅방 생성에 실패했습니다. 잠시 후 다시 시도해주세요! 🤔"
                            }
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }

    private fun navigateToChatList() {
        val activity = requireActivity()
        if (activity is DogCardMainActivity) {
            activity.navigateToChatTab()
        }
    }

    private fun navigateToChatRoom(chatId: Int, friendName: String) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("friend_name", friendName)
            putExtra("is_new_chat", true)
        }
        startActivity(intent)
    }
} 
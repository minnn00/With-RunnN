package com.with_runn.ui.friend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R
import com.with_runn.ui.chat.repository.ChatRepository
import com.with_runn.ui.chat.activity.ChatRoomActivity
import com.with_runn.ui.friend.adapter.RecommendedFriendAdapter
import com.with_runn.ui.friend.viewmodel.RecommendedFriendViewModel

class AllFriendsFragment : Fragment() {
    
    private lateinit var friendsAdapter: RecommendedFriendAdapter
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchButton: ImageView
    private lateinit var viewModel: RecommendedFriendViewModel
    private val chatRepository = ChatRepository()
    private var allFriends: List<com.with_runn.ui.friend.model.dto.RecommendedFriendResponse> = emptyList()
    
    // 검색 디바운싱을 위한 Handler
    private val searchHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupSearchUI()
        setupRecyclerView()
        setupObservers()
        
        // 기본값으로 모든 친구 조회 (백엔드 요청: provinceId = 9)
        loadAllFriends()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Handler 정리
        searchHandler.removeCallbacksAndMessages(null)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[RecommendedFriendViewModel::class.java]
    }

    private fun setupRecyclerView() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.friends_grid)
        
        // 2열 그리드 레이아웃 설정
        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView?.layoutManager = layoutManager
        
        // 어댑터 설정 (카드형 레이아웃 유지)
        friendsAdapter = RecommendedFriendAdapter(
            onItemClick = { friend ->
                // 프로필 다이얼로그 표시
                showFriendProfileDialog(friend)
            },
            layoutType = RecommendedFriendAdapter.LayoutType.Card
        )
        recyclerView?.adapter = friendsAdapter
    }

    private fun setupSearchUI() {
        searchEditText = view?.findViewById(R.id.search_edit_text)!!
        clearSearchButton = view?.findViewById(R.id.clear_search_button)!!
        
        // 검색 텍스트 변경 리스너
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val searchQuery = s.toString().trim()
                
                // 검색어가 있으면 지우기 버튼 표시
                clearSearchButton.visibility = if (searchQuery.isNotEmpty()) View.VISIBLE else View.GONE
                
                // 이전 검색 요청 취소
                searchHandler.removeCallbacks(searchRunnable)
                
                // 500ms 후에 검색 실행 (디바운싱)
                searchHandler.postDelayed({
                    filterFriends(searchQuery)
                }, 500)
            }
        })
        
        // 지우기 버튼 클릭 리스너
        clearSearchButton.setOnClickListener {
            searchEditText.text.clear()
            clearSearchButton.visibility = View.GONE
            // 이전 검색 요청 취소
            searchHandler.removeCallbacks(searchRunnable)
            filterFriends("")
        }
    }

    private fun setupObservers() {
        viewModel.allFriends.observe(viewLifecycleOwner) { friends ->
            allFriends = friends
            if (friends.isEmpty()) {
                showEmptyState("현재 친구가 없습니다.\n다른 지역을 선택하거나 나중에 다시 시도해보세요.")
            } else {
                hideEmptyState()
                friendsAdapter.updateFriends(friends)
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            if (searchResults.isNotEmpty()) {
                hideEmptyState()
                friendsAdapter.updateFriends(searchResults)
            } else {
                // 검색 결과가 없을 때만 빈 상태 표시 (검색 중일 때는 로딩 상태 유지)
                if (viewModel.isLoading.value == false) {
                    val searchQuery = searchEditText.text.toString().trim()
                    if (searchQuery.isNotEmpty()) {
                        showEmptyState("'${searchQuery}'에 대한 검색 결과가 없습니다.\n다른 키워드로 검색해보세요.")
                    }
                }
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                val searchQuery = searchEditText.text.toString().trim()
                if (searchQuery.isNotEmpty()) {
                    // 검색 중일 때는 검색 중임을 표시
                    showLoadingState("'${searchQuery}' 검색 중...")
                } else {
                    showLoadingState()
                }
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

    private fun showEmptyState(message: String) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.friends_grid)
        val emptyTextView = view?.findViewById<TextView>(R.id.emptyStateTextView)
        
        recyclerView?.visibility = View.GONE
        emptyTextView?.visibility = View.VISIBLE
        emptyTextView?.text = message
    }

    private fun hideEmptyState() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.friends_grid)
        val emptyTextView = view?.findViewById<TextView>(R.id.emptyStateTextView)
        
        recyclerView?.visibility = View.VISIBLE
        emptyTextView?.visibility = View.GONE
    }

    private fun showLoadingState(message: String? = null) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.friends_grid)
        val loadingTextView = view?.findViewById<TextView>(R.id.loadingTextView)
        
        recyclerView?.visibility = View.GONE
        loadingTextView?.visibility = View.VISIBLE
        loadingTextView?.text = message ?: "로딩 중..."
    }

    private fun hideLoadingState() {
        val loadingTextView = view?.findViewById<TextView>(R.id.loadingTextView)
        loadingTextView?.visibility = View.GONE
    }

    private fun loadAllFriends(provinceId: Int = 9, cityId: Int? = null, townId: Int? = null) {
        viewModel.loadAllFriends(provinceId, cityId, townId)
    }

    private fun filterFriends(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            // 검색어가 비어있으면 모든 친구 다시 로드
            loadAllFriends()
            viewModel.clearSearchResults()
        } else {
            // 검색어가 있으면 API 검색 실행
            viewModel.searchFriends(
                provinceId = 9, // 백엔드 요청: provinceId=9
                cityId = null,
                townId = null,
                keyword = searchQuery
            )
        }
    }
    
    /**
     * 친구 프로필 다이얼로그 표시
     */
    private fun showFriendProfileDialog(friend: com.with_runn.ui.friend.model.dto.RecommendedFriendResponse) {
        val dialogFragment = FriendProfileDialogFragment.newInstance(
            friendName = friend.userName ?: "",
            personalityTag = friend.style?.firstOrNull() ?: "",
            personalityTags = ArrayList((friend.style ?: emptyList()) + (friend.characters ?: emptyList())),
            imageResId = R.drawable.default_profile,
            userId = friend.userId
        )
        
        // 메시지 버튼 클릭 리스너 설정
        dialogFragment.setOnMessageButtonClickListener {
            // 채팅방 생성 API 호출
            createChatRoomWithFriend(friend)
        }
        
        dialogFragment.show(childFragmentManager, "FriendProfileDialog")
    }
    
    /**
     * 친구와 채팅방 생성
     */
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
    
    /**
     * 채팅 목록으로 이동
     */
    private fun navigateToChatList() {
        val activity = requireActivity()
        if (activity is DogCardMainActivity) {
            activity.navigateToChatTab()
        }
    }
    
    /**
     * 생성된 채팅방으로 이동
     */
    private fun navigateToChatRoom(chatId: Int, friendName: String) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("friend_name", friendName)
            putExtra("is_new_chat", true)
        }
        startActivity(intent)
    }
}
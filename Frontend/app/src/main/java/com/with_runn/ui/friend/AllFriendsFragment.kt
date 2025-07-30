package com.with_runn.ui.friend

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R
import com.with_runn.ui.chat.repository.ChatRepository
import com.with_runn.ui.chat.activity.ChatRoomActivity
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AllFriendsFragment : Fragment() {
    
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var allFriends: List<Friend>
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchButton: ImageView
    private val chatRepository = ChatRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 검색 UI 설정
        setupSearchUI()
        
        // RecyclerView 설정
        setupRecyclerView()
        
        // 샘플 데이터 로드
        loadSampleData()
    }

    private fun setupRecyclerView() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.friends_grid)
        
        // 2열 그리드 레이아웃 설정
        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView?.layoutManager = layoutManager
        
        // 어댑터 설정
        friendsAdapter = FriendsAdapter()
        recyclerView?.adapter = friendsAdapter
        
        // 카드 클릭 리스너 설정
        friendsAdapter.setOnItemClickListener { friend ->
            // 프로필 다이얼로그 표시
            showFriendProfileDialog(friend)
        }
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

    private fun loadSampleData() {
        allFriends = listOf(
            Friend(
                name = "마루",
                personalityTag = "#호기심 쟁이",
                personalityTags = listOf("#차분함", "#똑똑함"),
                imageResId = R.drawable.maru
            ),
            Friend(
                name = "룽이",
                personalityTag = "#우리 친해질래?",
                personalityTags = listOf("#조용함", "#사교적"),
                imageResId = R.drawable.roongji
            ),
            Friend(
                name = "홍이",
                personalityTag = "#달리기 실시",
                personalityTags = listOf("#독립적", "#에너지 폭발"),
                imageResId = R.drawable.hongi
            ),
            Friend(
                name = "구리",
                personalityTag = "#같이 놀자",
                personalityTags = listOf("#느긋함", "#스퀸십좋아함"),
                imageResId = R.drawable.guri
            ),
        )
        
        friendsAdapter.updateFriends(allFriends)
    }

    private fun filterFriends(searchQuery: String) {
        val filteredFriends = if (searchQuery.isEmpty()) {
            allFriends
        } else {
            allFriends.filter { friend ->
                friend.name.contains(searchQuery, ignoreCase = true) ||
                friend.personalityTag.contains(searchQuery, ignoreCase = true) ||
                friend.personalityTags.any { it.contains(searchQuery, ignoreCase = true) }
            }
        }
        
        friendsAdapter.updateFriends(filteredFriends)
    }
    
    /**
     * 친구 프로필 다이얼로그 표시
     */
    private fun showFriendProfileDialog(friend: Friend) {
        val dialogFragment = FriendProfileDialogFragment.newInstance(
            friend.name,
            friend.personalityTag,
            ArrayList(friend.personalityTags),
            friend.imageResId
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
    private fun createChatRoomWithFriend(friend: Friend) {
        Log.d("AllFriendsFragment", "=== 채팅방 생성 시작 ===")
        Log.d("AllFriendsFragment", "선택된 친구: ${friend.name}")
        
        // 로딩 표시 (선택적으로 구현 가능)
        Toast.makeText(requireContext(), "${friend.name}님과 채팅방을 생성 중입니다...", Toast.LENGTH_SHORT).show()
        
        // 테스트용 사용자 ID (실제로는 로그인한 사용자 ID를 사용)
        val currentUserId = 1
        val targetUserId = getTargetUserId(friend.name) // 친구 이름으로 targetUserId 매핑
        
        Log.d("AllFriendsFragment", "API 호출 파라미터: currentUserId=$currentUserId, targetUserId=$targetUserId")
        
        chatRepository.createChatRoom(currentUserId, targetUserId) { result ->
            requireActivity().runOnUiThread {
                result.fold(
                    onSuccess = {
                        Log.d("AllFriendsFragment", "✅ 채팅방 생성 성공!")
                        Toast.makeText(requireContext(), "${friend.name}님과의 채팅방이 생성되었습니다!", Toast.LENGTH_SHORT).show()
                        
                        // 채팅방으로 이동
                        navigateToChatRoom(friend)
                    },
                    onFailure = { exception ->
                        Log.e("AllFriendsFragment", "❌ 채팅방 생성 실패", exception)
                        Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
    
    /**
     * 친구 이름으로 targetUserId 매핑 (임시 구현)
     */
    private fun getTargetUserId(friendName: String): Int {
        return when (friendName) {
            "마루" -> 2
            "룽이" -> 3
            "홍이" -> 4
            "구리" -> 5
            else -> 999 // 기본값
        }
    }
    
    /**
     * 채팅방으로 이동
     */
    private fun navigateToChatRoom(friend: Friend) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
            // 친구 정보를 전달
            putExtra("friend_name", friend.name)
            putExtra("friend_image", friend.imageResId)
            putExtra("is_new_chat", true) // 새 채팅방 플래그
        }
        startActivity(intent)
    }
} 
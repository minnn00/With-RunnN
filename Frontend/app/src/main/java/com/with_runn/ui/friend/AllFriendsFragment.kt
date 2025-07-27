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

class AllFriendsFragment : Fragment() {
    
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var allFriends: List<Friend>
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchButton: ImageView

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
            val dialogFragment = FriendProfileDialogFragment.newInstance(
                friend.name,
                friend.personalityTag,
                ArrayList(friend.personalityTags),
                friend.imageResId
            )
            dialogFragment.show(childFragmentManager, "FriendProfileDialog")
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
} 
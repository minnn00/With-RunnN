package com.with_runn.ui.chat.dialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.with_runn.ui.friend.Friend
import com.with_runn.ui.friend.FriendAddAdapter
import com.with_runn.R
import com.with_runn.ui.chat.repository.ChatRepository

class AddParticipantBottomSheet : BottomSheetDialogFragment() {

    private lateinit var searchInput: EditText
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var inviteButton: TextView
    private lateinit var friendAdapter: FriendAddAdapter
    private val chatRepository = ChatRepository()
    
    private var onParticipantAddedListener: ((String) -> Unit)? = null
    
    fun setOnParticipantAddedListener(listener: (String) -> Unit) {
        onParticipantAddedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_participant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupRecyclerView()
        loadFriends()
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        searchInput = view.findViewById(R.id.search_input)
        friendsRecyclerView = view.findViewById(R.id.friends_recycler_view)
        inviteButton = view.findViewById(R.id.invite_button)
    }

    private fun setupRecyclerView() {
        friendAdapter = FriendAddAdapter()
        friendsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = friendAdapter
        }
    }

    private fun loadFriends() {
        val friends = listOf(
            Friend("마루", imageResId = R.drawable.maru, isSelected = true),
            Friend("조이", imageResId = R.drawable.maru),
            Friend("위니", imageResId = R.drawable.maru),
            Friend("구리", imageResId = R.drawable.maru),
            Friend("룽지", imageResId = R.drawable.maru),
            Friend("솜이", imageResId = R.drawable.maru)
        )
        friendAdapter.submitList(friends)
    }

    private fun setupClickListeners() {
        inviteButton.setOnClickListener {
            Log.d("AddParticipant", "초대하기 버튼 클릭됨")
            val selectedFriends = friendAdapter.getSelectedFriends()
            Log.d("AddParticipant", "선택된 친구 수: ${selectedFriends.size}")
            
            if (selectedFriends.isNotEmpty()) {
                // API 호출을 통한 실제 초대
                callInviteAPI(selectedFriends)
            } else {
                Toast.makeText(context, "초대할 친구를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * API를 통한 실제 초대 호출
     */
    private fun callInviteAPI(selectedFriends: List<Friend>) {
        Log.d("AddParticipant", "=== API 초대 호출 시작 ===")
        
        // 임시로 chatId 1 사용 (실제로는 현재 채팅방 ID를 받아와야 함)
        val chatId = 1
        val userIds = selectedFriends.map { friend ->
            // 임시로 친구 이름을 ID로 변환 (실제로는 친구의 실제 ID를 사용해야 함)
            when (friend.name) {
                "마루" -> 1
                "조이" -> 2
                "위니" -> 3
                "구리" -> 4
                "룽지" -> 5
                "솜이" -> 6
                else -> 1
            }
        }
        
        Log.d("AddParticipant", "API 호출 파라미터: chatId=$chatId, userIds=$userIds")
        
        chatRepository.inviteUsers(chatId, userIds) { result ->
            result.fold(
                onSuccess = { invitedChatId ->
                    Log.d("AddParticipant", "✅ API 초대 성공! chatId=$invitedChatId")
                    
                    // UI 업데이트 (기존 로직)
                    selectedFriends.forEach { friend ->
                        Log.d("AddParticipant", "선택된 친구: ${friend.name}")
                        onParticipantAddedListener?.invoke(friend.name)
                    }
                    
                    // 성공 메시지 표시
                    activity?.runOnUiThread {
                        Toast.makeText(context, "초대가 완료되었습니다!", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                },
                onFailure = { exception ->
                    Log.e("AddParticipant", "❌ API 초대 실패", exception)
                    
                    // 실패 시에도 UI는 업데이트 (기존 로직 유지)
                    selectedFriends.forEach { friend ->
                        Log.d("AddParticipant", "선택된 친구: ${friend.name}")
                        onParticipantAddedListener?.invoke(friend.name)
                    }
                    
                    // 실패 메시지 표시
                    activity?.runOnUiThread {
                        Toast.makeText(context, "초대에 실패했습니다: ${exception.message}", Toast.LENGTH_LONG).show()
                        dismiss()
                    }
                }
            )
        }
    }
} 
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
import com.with_runn.ui.chat.model.dto.InviteUserDto

class AddParticipantBottomSheet : BottomSheetDialogFragment() {

    private lateinit var searchInput: EditText
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var inviteButton: TextView
    private lateinit var friendAdapter: FriendAddAdapter
    private val chatRepository = ChatRepository()
    
    private var onParticipantAddedListener: ((String) -> Unit)? = null
    private var chatId: Int = -1
    
    fun setOnParticipantAddedListener(listener: (String) -> Unit) {
        onParticipantAddedListener = listener
    }
    
    fun setChatId(id: Int) {
        chatId = id
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
        loadInviteUserList()
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

    /**
     * API를 통해 초대 가능한 사용자 목록을 불러옴
     */
    private fun loadInviteUserList() {
        if (chatId == -1) {
            Log.e("AddParticipant", "chatId가 설정되지 않았습니다")
            return
        }
        
        Log.d("AddParticipant", "초대 목록 조회 시작: chatId=$chatId")
        
        chatRepository.getInviteUserList(chatId) { result ->
            result.fold(
                onSuccess = { inviteUserList ->
                    Log.d("AddParticipant", "✅ 초대 목록 조회 성공: ${inviteUserList.size}명")
                    
                    // InviteUserDto를 Friend 모델로 변환
                    val friends = inviteUserList.map { inviteUser ->
                        Friend(
                            name = inviteUser.name,
                            imageResId = R.drawable.maru, // 기본 이미지 사용
                            isSelected = false
                        )
                    }
                    
                    activity?.runOnUiThread {
                        friendAdapter.submitList(friends)
                    }
                },
                onFailure = { exception ->
                    Log.e("AddParticipant", "❌ 초대 목록 조회 실패", exception)
                    
                    // 실패 시 샘플 데이터 로드
                    activity?.runOnUiThread {
                        loadSampleData()
                        Toast.makeText(context, "초대 목록을 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
    
    /**
     * 샘플 데이터 로드 (API 실패 시 사용)
     */
    private fun loadSampleData() {
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
        
        if (chatId == -1) {
            Toast.makeText(context, "채팅방 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 현재 사용자 이름 (실제로는 로그인된 사용자 정보에서 가져와야 함)
        val username = "현재사용자"
        
        // InviteUser 리스트 생성
        val inviteUserList = selectedFriends.map { friend ->
            val userId = when (friend.name) {
                "마루" -> 1
                "조이" -> 2
                "위니" -> 3
                "구리" -> 4
                "룽지" -> 5
                "솜이" -> 6
                else -> 1
            }
            com.with_runn.ui.chat.model.dto.InviteUser(friend.name, userId)
        }
        
        Log.d("AddParticipant", "API 호출 파라미터: chatId=$chatId, username=$username, inviteUserList=$inviteUserList")
        
        chatRepository.inviteUsers(chatId, username, inviteUserList) { result ->
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
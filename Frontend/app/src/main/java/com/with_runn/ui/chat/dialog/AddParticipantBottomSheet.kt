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

class AddParticipantBottomSheet : BottomSheetDialogFragment() {

    private lateinit var searchInput: EditText
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var inviteButton: TextView
    private lateinit var friendAdapter: FriendAddAdapter
    
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
                // 선택된 친구들을 현재 채팅방에 추가
                selectedFriends.forEach { friend ->
                    Log.d("AddParticipant", "선택된 친구: ${friend.name}")
                    onParticipantAddedListener?.invoke(friend.name)
                }
                dismiss()
            } else {
                Toast.makeText(context, "초대할 친구를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 
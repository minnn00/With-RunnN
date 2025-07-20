package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddParticipantBottomSheet : BottomSheetDialogFragment() {

    private lateinit var searchInput: EditText
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var inviteButton: Button
    private lateinit var friendAdapter: FriendAddAdapter

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
            Friend("마루", imageResId = R.drawable.ellipse_50, isSelected = true),
            Friend("조이", imageResId = R.drawable.ellipse_51),
            Friend("위니", imageResId = R.drawable.ellipse_52),
            Friend("구리", imageResId = R.drawable.ellipse_50),
            Friend("룽지", imageResId = R.drawable.ellipse_51),
            Friend("솜이", imageResId = R.drawable.ellipse_52)
        )
        friendAdapter.submitList(friends)
    }

    private fun setupClickListeners() {
        inviteButton.setOnClickListener {
            val selectedFriends = friendAdapter.getSelectedFriends()
            if (selectedFriends.isNotEmpty()) {
                val friendNames = selectedFriends.joinToString(", ") { it.name }
                Toast.makeText(context, "$friendNames 님을 초대했습니다!", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context, "초대할 친구를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 
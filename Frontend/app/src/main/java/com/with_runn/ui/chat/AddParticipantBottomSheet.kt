package com.with_runn.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.with_runn.data.Friend
import com.with_runn.data.Gender
import com.with_runn.databinding.BottomSheetAddParticipantBinding

class AddParticipantBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddParticipantBinding? = null
    private val binding get() = _binding!!

    private lateinit var friendsAdapter: SelectableFriendAdapter
    private var allFriends: List<Friend> = emptyList()
    private var selectedFriends: Set<Friend> = emptySet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddParticipantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        loadFriends()
    }

    private fun setupRecyclerView() {
        friendsAdapter = SelectableFriendAdapter { friend, isSelected ->
            selectedFriends = if (isSelected) {
                selectedFriends + friend
            } else {
                selectedFriends - friend
            }
            updateNextButton()
        }

        binding.rvSelectableFriends.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = friendsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            addSelectedFriends()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            filterFriends(query)
        }
    }

    private fun loadFriends() {
        // 임시 친구 데이터 (실제로는 API에서 가져와야 함)
        allFriends = listOf(
            Friend(
                id = "f1",
                name = "루나",
                imageUrl = "",
                tags = listOf("온순함", "조용함"),
                bio = "루나예요!",
                age = "1년 8개월",
                ageInMonths = 20,
                breed = "포메라니안",
                category = "소형견",
                gender = Gender.FEMALE,
                personality = listOf("온순함", "조용함", "애교많음"),
                walkingStyle = listOf("짧은 산책", "천천히 걷기")
            ),
            Friend(
                id = "f2",
                name = "바둑이",
                imageUrl = "",
                tags = listOf("사교적", "영리함"),
                bio = "바둑이입니다!",
                age = "3년 1개월",
                ageInMonths = 37,
                breed = "믹스견",
                category = "중형견",
                gender = Gender.MALE,
                personality = listOf("사교적", "영리함", "활발함"),
                walkingStyle = listOf("중거리 산책", "공놀이")
            ),
            Friend(
                id = "f3",
                name = "초코",
                imageUrl = "",
                tags = listOf("차분함", "애교"),
                bio = "초코예요!",
                age = "4년 2개월",
                ageInMonths = 50,
                breed = "래브라도",
                category = "대형견",
                gender = Gender.FEMALE,
                personality = listOf("차분함", "애교많음", "친근함"),
                walkingStyle = listOf("장거리 산책", "훈련하며 산책")
            ),
            Friend(
                id = "f4",
                name = "코코",
                imageUrl = "",
                tags = listOf("장난스러움", "호기심"),
                bio = "코코입니다!",
                age = "1년 6개월",
                ageInMonths = 18,
                breed = "비글",
                category = "중형견",
                gender = Gender.MALE,
                personality = listOf("장난스러움", "호기심많음", "활발함"),
                walkingStyle = listOf("중거리 산책", "장난감 놀이")
            ),
            Friend(
                id = "f5",
                name = "몰리",
                imageUrl = "",
                tags = listOf("활발함", "친화적"),
                bio = "몰리예요!",
                age = "2년 6개월",
                ageInMonths = 30,
                breed = "보더콜리",
                category = "중형견",
                gender = Gender.FEMALE,
                personality = listOf("활발함", "친화적", "똑똑함"),
                walkingStyle = listOf("장거리 산책", "훈련하며 산책")
            )
        )
        friendsAdapter.submitList(allFriends)
    }

    private fun filterFriends(query: String) {
        val filteredList = if (query.isEmpty()) {
            allFriends
        } else {
            allFriends.filter { friend ->
                friend.name.contains(query, ignoreCase = true) ||
                        friend.breed.contains(query, ignoreCase = true)
            }
        }
        friendsAdapter.submitList(filteredList)
    }

    private fun updateNextButton() {
        binding.btnNext.isEnabled = selectedFriends.isNotEmpty()
    }

    private fun addSelectedFriends() {
        if (selectedFriends.isEmpty()) return

        val friendNames = selectedFriends.map { it.name }.joinToString(", ")
        Toast.makeText(
            requireContext(),
            "$friendNames 님이 채팅방에 추가되었습니다",
            Toast.LENGTH_SHORT
        ).show()

        // TODO: 실제 API 호출로 대화 상대 추가
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): AddParticipantBottomSheet {
            return AddParticipantBottomSheet()
        }
    }
} 
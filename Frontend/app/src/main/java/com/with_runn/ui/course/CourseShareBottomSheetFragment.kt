package com.with_runn.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.with_runn.data.ShareTarget
import com.with_runn.databinding.CourseShareBinding
import com.with_runn.R

class CourseShareBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: CourseShareBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CourseShareAdapter
    private var selectedTarget: ShareTarget? = null

    // 채팅방과 친구 더미 데이터
    private val chatRoomList = listOf(
        ShareTarget("초코, 모찌", R.drawable.img),
        ShareTarget("연남강아지모임", R.drawable.img),
        ShareTarget("조이", R.drawable.img)
    )
    private val friendList = listOf(
        ShareTarget("조니", R.drawable.img),
        ShareTarget("조이", R.drawable.img),
        ShareTarget("위니", R.drawable.img)
    )



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CourseShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CourseShareAdapter { selected ->
            selectedTarget = selected
            updateSendButtonState()
        }

        binding.recyclerTargets.adapter = adapter
        binding.recyclerTargets.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.tabChat.setOnClickListener { showChatTab() }
        binding.tabFriend.setOnClickListener { showFriendTab() }

        showChatTab()
    }

    private fun showChatTab() {
        binding.indicatorChat.visibility = View.VISIBLE
        binding.indicatorFriend.visibility = View.GONE
        adapter.submitList(chatRoomList)
        selectedTarget = null
        updateSendButtonState()
    }

    private fun showFriendTab() {
        binding.indicatorChat.visibility = View.GONE
        binding.indicatorFriend.visibility = View.VISIBLE
        adapter.submitList(friendList)
        selectedTarget = null
        updateSendButtonState()
    }

    private fun updateSendButtonState() {
        val isEnabled = selectedTarget != null
        binding.btnSend.isEnabled = isEnabled
        Log.d("ShareBottomSheet", "Send button enabled: $isEnabled")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
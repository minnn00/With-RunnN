package com.with_runn.ui.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.with_runn.R
import com.with_runn.databinding.FragmentFriendsTabBinding
import com.with_runn.ui.location.LocationActivity

class FriendsTabFragment : Fragment() {

    private var _binding: FragmentFriendsTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupClickListeners()
    }

    private fun setupViewPager() {
        val adapter = FriendsTabAdapter(this)
        binding.viewPager.adapter = adapter
        
        // 탭 간 스와이프 기능 비활성화
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "추천 친구"
                1 -> "모두 보기"
                else -> ""
            }
        }.attach()
    }

    private fun setupClickListeners() {
        binding.apply {
            // 위치 클릭 - LocationActivity로 이동
            llLocation.setOnClickListener {
                val intent = Intent(requireContext(), LocationActivity::class.java)
                startActivity(intent)
            }

            // 알림 클릭 - NotificationFragment로 이동
            ivNotifications.setOnClickListener {
                findNavController().navigate(R.id.action_friends_to_notification)
            }

            // 채팅 클릭 - ChatListFragment로 이동
            ivChat.setOnClickListener {
                findNavController().navigate(R.id.action_friends_to_chat_list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class FriendsTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RecommendedFriendsFragment()
                1 -> FriendsSeeAllFragment() // 기존에 구현된 Fragment 재사용
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
} 
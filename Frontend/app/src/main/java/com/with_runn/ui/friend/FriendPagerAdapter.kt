package com.with_runn.ui.friend

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FriendPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2  // 탭 수 (추천 친구, 모두 보기)

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecommendedFriendsFragment()
            1 -> AllFriendsFragment()
            else -> throw IndexOutOfBoundsException("Invalid tab position: $position")
        }
    }
}

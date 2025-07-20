package com.with_runn.ui.mypage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MypageFollowersFragmentAdaper(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    private val pageTexts = listOf("모든 팔로워", "모든 팔로잉")

    override fun createFragment(position: Int): Fragment {
        return MypageFollowersFragment.newInstance(pageTexts[position])
    }
}
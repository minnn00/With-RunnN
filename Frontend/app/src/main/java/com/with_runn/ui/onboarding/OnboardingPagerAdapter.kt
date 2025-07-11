package com.with_runn.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity,
                             private val title: List<String>,
                             private val description: List<String>,
                             private val imgID: List<Int>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment1.newInstance(title[position], description[position], imgID[position])
    }
}
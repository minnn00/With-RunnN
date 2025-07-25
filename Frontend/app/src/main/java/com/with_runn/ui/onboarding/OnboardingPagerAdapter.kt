package com.with_runn.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(
    fragment: Fragment,
    private val title: List<String>,
    private val description: List<String>,
    private val imgID: List<Int>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = title.size

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment1.newInstance(
            title[position],
            description[position],
            imgID[position]
        )
    }
}

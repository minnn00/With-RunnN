package com.with_runn.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.with_runn.ActivityViewModel
import com.with_runn.databinding.FragmentMypageFollowerFollowBinding

class MypageFollowerFollowFragment : Fragment() {

    private var _binding: FragmentMypageFollowerFollowBinding? = null
    private val binding get() = _binding!!

    private val activityVM : ActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageFollowerFollowBinding.inflate(inflater, container, false)

        activityVM.setBottomNavVisibility(false)
        activityVM.setUpperToolbarVisibility(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager2
        val tabLayout = binding.tabLayout

        val adapter = MypageFollowersFragmentAdaper(requireActivity())
        viewPager.adapter = adapter

        val tabTitles = listOf("팔로워", "팔로우")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val initialTab = arguments?.getInt("initialTab") ?: 0
        binding.viewPager2.setCurrentItem(initialTab, false)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

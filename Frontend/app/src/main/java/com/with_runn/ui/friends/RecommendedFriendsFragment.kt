package com.with_runn.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.with_runn.data.Friend
import com.with_runn.data.Gender
import com.with_runn.databinding.FragmentRecommendedFriendsBinding

class RecommendedFriendsFragment : Fragment() {

    private var _binding: FragmentRecommendedFriendsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewPagerAdapter: RecommendedFriendsAdapter
    private val friends = mutableListOf<Friend>()
    private var currentPosition = 0
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendedFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadRecommendedFriends()
        setupViewPager()
        setupClickListeners()
    }

    private fun setupViewPager() {
        viewPagerAdapter = RecommendedFriendsAdapter(friends) { friend ->
            // 카드 클릭 시 프로필 상세 보기
            showFriendDetail(friend)
        }

        binding.viewPagerRecommendedFriends.adapter = viewPagerAdapter
        binding.viewPagerRecommendedFriends.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPagerRecommendedFriends.offscreenPageLimit = 1
        // 카드 스와이프 기능 유지 (기본값이 true이므로 명시적 설정)
        binding.viewPagerRecommendedFriends.isUserInputEnabled = true

        // 페이지 변경 리스너
        binding.viewPagerRecommendedFriends.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }
        })

        // TabLayoutMediator로 ViewPager2와 TabLayout 연결
        tabLayoutMediator = TabLayoutMediator(
            binding.tabLayoutIndicator,
            binding.viewPagerRecommendedFriends
        ) { tab, position ->
            // 탭에 텍스트를 설정할 필요가 없으므로 비워둠
            // 점 인디케이터는 tabBackground에서 자동으로 처리됨
        }
        tabLayoutMediator.attach()

        updateEmptyState()
    }

    private fun setupClickListeners() {
        // FAB 버튼들이 제거되었으므로 클릭 리스너 불필요
        // 카드 스와이프 기능은 ViewPager2에서 자동으로 처리
    }

    private fun loadRecommendedFriends() {
        // 임시 데이터 (실제로는 API에서 가져와야 함)
        val sampleFriends = listOf(
            Friend(
                id = "1",
                name = "마루",
                imageUrl = "",
                tags = listOf("활발함", "친화적", "실외활동"),
                bio = "안녕하세요! 마루입니다. 공원에서 뛰어다니는 걸 좋아해요.",
                age = "2년 4개월",
                breed = "골든리트리버",
                category = "대형견",
                gender = Gender.MALE
            ),
            Friend(
                id = "2",
                name = "루나",
                imageUrl = "",
                tags = listOf("온순함", "조용함", "실내활동"),
                bio = "루나예요! 집에서 조용히 지내는 걸 좋아해요.",
                age = "1년 8개월",
                breed = "포메라니안",
                category = "소형견",
                gender = Gender.FEMALE
            ),
            Friend(
                id = "3",
                name = "바둑이",
                imageUrl = "",
                tags = listOf("사교적", "영리함", "훈련"),
                bio = "바둑이입니다! 다른 강아지들과 어울리는 걸 좋아해요.",
                age = "3년 1개월",
                breed = "믹스견",
                category = "중형견",
                gender = Gender.MALE
            ),
            Friend(
                id = "4",
                name = "초코",
                imageUrl = "",
                tags = listOf("차분함", "애교", "간식"),
                bio = "초코예요! 간식을 좋아하고 애교가 많아요.",
                age = "4년 2개월",
                breed = "래브라도",
                category = "대형견",
                gender = Gender.FEMALE
            ),
            Friend(
                id = "5",
                name = "코코",
                imageUrl = "",
                tags = listOf("장난스러움", "호기심", "모험"),
                bio = "코코입니다! 새로운 곳을 탐험하는 걸 좋아해요.",
                age = "1년 6개월",
                breed = "비글",
                category = "중형견",
                gender = Gender.MALE
            )
        )

        friends.addAll(sampleFriends)
        if (::viewPagerAdapter.isInitialized) {
            viewPagerAdapter.notifyDataSetChanged()
        }
    }



    private fun showFriendDetail(friend: Friend) {
        // 친구 상세 정보 다이얼로그 표시
        val dialog = FriendDetailDialogFragment.newInstance(friend)
        dialog.show(parentFragmentManager, "FriendDetail")
    }

    private fun updateEmptyState() {
        val isEmpty = friends.isEmpty()
        binding.llEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.tabLayoutIndicator.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // TabLayoutMediator 해제
        if (::tabLayoutMediator.isInitialized) {
            tabLayoutMediator.detach()
        }
        _binding = null
    }
} 
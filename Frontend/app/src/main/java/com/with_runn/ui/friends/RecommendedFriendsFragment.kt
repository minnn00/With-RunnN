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
        binding.tabLayoutIndicator.removeAllTabs()
        loadRecommendedFriends()
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.tabLayoutIndicator.removeAllTabs()
        
        viewPagerAdapter = RecommendedFriendsAdapter(friends) { friend ->
            showFriendDetail(friend)
        }

        binding.viewPagerRecommendedFriends.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
            isUserInputEnabled = true

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPosition = position
                }
            })
        }

        tabLayoutMediator = TabLayoutMediator(
            binding.tabLayoutIndicator,
            binding.viewPagerRecommendedFriends
        ) { _, _ -> }
        tabLayoutMediator.attach()

        // 인디케이터 점들 사이 간격 12dp(좌우 6dp) 적용
        addTabIndicatorMargin()

        updateEmptyState()
    }

    private fun loadRecommendedFriends() {
        val sampleFriends = listOf(
            Friend(
                id = "1",
                name = "조니",
                imageUrl = "",
                tags = listOf("도그워커", "산책메이트"),
                bio = "안녕하세요! 달리기를 좋아하는 3살 조니예요 :)",
                age = "3살",
                ageInMonths = 36,
                breed = "골든리트리버",
                category = "대형견",
                gender = Gender.MALE,
                personality = listOf("활발함", "친근함", "사교적"),
                walkingStyle = listOf("장거리 산책", "달리기", "공놀이")
            ),
            Friend(
                id = "2",
                name = "홍이",
                imageUrl = "",
                tags = listOf("조용함", "사교적"),
                bio = "홍이예요! 조용하지만 사교적인 강아지입니다.",
                age = "8개월",
                ageInMonths = 8,
                breed = "포메라니안",
                category = "소형견",
                gender = Gender.FEMALE,
                personality = listOf("조용함", "애교많음", "온순함"),
                walkingStyle = listOf("짧은 산책", "천천히 걷기")
            ),
            Friend(
                id = "3",
                name = "마루",
                imageUrl = "",
                tags = listOf("친절함", "똑똑함"),
                bio = "마루입니다! 친절하고 똑똑한 강아지예요.",
                age = "2살",
                ageInMonths = 24,
                breed = "웰시코기",
                category = "중형견",
                gender = Gender.MALE,
                personality = listOf("똑똑함", "활발함", "친근함"),
                walkingStyle = listOf("중거리 산책", "훈련하며 산책")
            ),
            Friend(
                id = "4",
                name = "보리",
                imageUrl = "",
                tags = listOf("똑바로", "에너지 폭발"),
                bio = "보리에요! 에너지 넘치는 강아지입니다.",
                age = "1살",
                ageInMonths = 12,
                breed = "시바견",
                category = "중형견",
                gender = Gender.MALE,
                personality = listOf("장난꾸러기", "활발함", "호기심많음"),
                walkingStyle = listOf("장거리 산책", "산책하며 훈련")
            ),
            Friend(
                id = "5",
                name = "몽이",
                imageUrl = "",
                tags = listOf("에너지 폭발", "강아지 친구 찾기중"),
                bio = "몽이입니다! 같이 놀 친구를 찾고 있어요!",
                age = "1살 6개월",
                ageInMonths = 18,
                breed = "말티즈",
                category = "소형견",
                gender = Gender.FEMALE,
                personality = listOf("애교많음", "사교적", "활발함"),
                walkingStyle = listOf("짧은 산책", "공원 산책")
            )
        )

        friends.clear()
        friends.addAll(sampleFriends)
        if (::viewPagerAdapter.isInitialized) {
            viewPagerAdapter.notifyDataSetChanged()
        }
    }

    private fun showFriendDetail(friend: Friend) {
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
        binding.tabLayoutIndicator.removeAllTabs()
        if (::tabLayoutMediator.isInitialized) {
            tabLayoutMediator.detach()
        }
        _binding = null
    }

    // TabLayout의 각 탭에 마진을 주는 함수
    private fun addTabIndicatorMargin() {
        val tabLayout = binding.tabLayoutIndicator
        tabLayout.post {
            for (i in 0 until tabLayout.tabCount) {
                val tab = (tabLayout.getChildAt(0) as? ViewGroup)?.getChildAt(i)
                val layoutParams = tab?.layoutParams as? ViewGroup.MarginLayoutParams
                layoutParams?.let {
                    it.setMargins(6.dp, 0, 6.dp, 0)
                    tab.layoutParams = it
                }
            }
        }
    }

    // dp to px 변환 확장 함수
    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
} 
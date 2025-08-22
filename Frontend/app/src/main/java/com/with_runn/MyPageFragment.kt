package com.with_runn

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.with_runn.data.course.CourseManageArgs
import com.with_runn.data.course.CourseMode
import com.with_runn.ui.MypageViewModel
import com.with_runn.databinding.FragmentMypageBinding
import com.with_runn.ui.MyPageLinearAdapter

class MyPageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val myPageVM : MypageViewModel by activityViewModels()
    private val activityVM : ActivityViewModel by activityViewModels()

    private lateinit var tabAdapter: MyPageLinearAdapter

    // 0: 스크랩, 1: 좋아요, 2: 나의 산책코스
    private var currentTabIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        activityVM.setUpperToolbarVisibility(false)
        activityVM.setBottomNavVisibility(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        setTabLayout()
        setBinding()
        setCoroutines()

        myPageVM.getProfile()

        renderCurrentTab()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCoroutines(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            myPageVM.scraps.collect { items ->
                if (currentTabIndex == 0) tabAdapter.submitList(items ?: emptyList())
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            myPageVM.likes.collect { items ->
                if (currentTabIndex == 1) tabAdapter.submitList(items ?: emptyList())
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            myPageVM.myCourses.collect { items ->
                if (currentTabIndex == 2) tabAdapter.submitList(items ?: emptyList())
            }
        }
    }

    private fun setBinding(){
        with(binding) {
            // 1) 최초 데이터 프리패치
            myPageVM.prefetchAll(force = false)

            // 프로필 바인딩
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                myPageVM.profile.collect { p ->
                    // 이름
                    textName.text = p?.name ?: "이름"

                    // 견종/크기/나이
                    tvMetaBreed.text = p?.breed ?: "견종"
                    tvMetaSize.text  = p?.size  ?: "크기"
                    tvMetaAge.text   = p?.birth ?: "나이"

                    // 성격/스타일 태그
                    val chars  = parseTagsAny(p?.characters)
                    val styles = parseTagsAny(p?.style)

                    tvCharMain.text = chars.firstOrNull() ?: "성격"
                    tvCharMore.apply {
                        val rest = (chars.size - 1).coerceAtLeast(0)
                        text = if (rest > 0) "외 ${rest}개" else ""
                        visibility = if (rest > 0) View.VISIBLE else View.GONE
                    }

                    tvStyleMain.text = styles.firstOrNull() ?: "산책 스타일"
                    tvStyleMore.apply {
                        val rest = (styles.size - 1).coerceAtLeast(0)
                        text = if (rest > 0) "외 ${rest}개" else ""
                        visibility = if (rest > 0) View.VISIBLE else View.GONE
                    }

                    Glide.with(imageProfile.context)
                        .load(p?.profileImage)
                        .centerCrop()
                        .placeholder(R.drawable.ic_fallback)
                        .error(R.drawable.ic_new_logo_gray)
                        .fallback(R.drawable.ic_new_logo_gray)
                        .into(imageProfile)

                    // 프로필 이미지는 ProfileData 스키마에 없으므로 보류(placeholder 유지)
                    // 이미지가 필요하면 ProfileData에 profileImage 추가 후 여기서 Glide 로드
                }
            }

            // 3) 팔로워/팔로잉 카운트
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                myPageVM.followers.collect { list ->
                    binding.textFollowerCount.text = (list?.size ?: 0).toString()
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                myPageVM.followings.collect { list ->
                    binding.textFollowingCount.text = (list?.size ?: 0).toString()
                }
            }

            settingBtn.setOnClickListener { findNavController().navigate(R.id.mypageOptionFragment) }

            layoutFollower.setOnClickListener { findNavController().navigate(R.id.mypageFollowerFollowFragment) }
            layoutFollowing.setOnClickListener { findNavController().navigate(R.id.mypageFollowerFollowFragment) }
        }
    }

    private fun setupRecycler() {
        tabAdapter = MyPageLinearAdapter(
            onItemClick = { course ->
                val bundle = Bundle().apply { putInt("courseId", course.courseId) }
                findNavController().navigate(R.id.courseDetailFragment, bundle)
            },
            onEditClick = { course ->
                val args = CourseManageArgs(
                    mode = CourseMode.EDIT,
                    courseId = course.courseId
                )
                val bundle = Bundle().apply {
                    putSerializable("args", args)
                }
                findNavController().navigate(R.id.courseManageFragment, bundle)
            },
            onDeleteClick = { course ->
                when (currentTabIndex) {
                    0 -> { // 스크랩 탭: 스크랩 해제
                        myPageVM.unbookmark(course.courseId) { ok ->
                            if (ok) tabAdapter.submitList(tabAdapter.currentList.filterNot { it.courseId == course.courseId })
                        }
                    }
                    1 -> { // 좋아요 탭: 좋아요 해제
                        myPageVM.unlike(course.courseId) { ok ->
                            if (ok) tabAdapter.submitList(tabAdapter.currentList.filterNot { it.courseId == course.courseId })
                        }
                    }
                    2 -> { // 내 코스: 서버 삭제 API 없음 → TODO
                        // TODO: API 생기면 ViewModel에 삭제 메서드 추가 후 여기 연결
                        tabAdapter.submitList(tabAdapter.currentList.filterNot { it.courseId == course.courseId })
                    }
                }
            }
        )
        binding.mypageRcv.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = this@MyPageFragment.tabAdapter
        }
    }

    private fun renderCurrentTab() {
        when (currentTabIndex) {
            0 -> tabAdapter.submitList(myPageVM.scraps.value)
            1 -> tabAdapter.submitList(myPageVM.likes.value)
            2 -> tabAdapter.submitList(myPageVM.myCourses.value)
        }
    }

    private fun setTabLayout(){
        // 기존 TabItem이 레이아웃에 있더라도, 안전하게 텍스트 채워주기(없으면 addTab)
        val titles = listOf("스크랩", "좋아요", "나의 산책코스")
        val tabLayout = binding.tabLayout

        if (tabLayout.tabCount == 0) {
            titles.forEach { title ->
                tabLayout.addTab(tabLayout.newTab().setText(title))
            }
        } else {
            for (i in 0 until minOf(tabLayout.tabCount, titles.size)) {
                tabLayout.getTabAt(i)?.text = titles[i]
            }
        }

        // 기본 선택
        tabLayout.getTabAt(currentTabIndex)?.select()

        // 탭 전환 리스너(여기서는 인덱스만 관리)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTabIndex = tab.position
                renderCurrentTab()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                // TODO: 필요 시 당겨새로고침/스크롤탑 등
            }
        })
        renderCurrentTab()
    }
    private fun parseTagsAny(list: List<String>?): List<String> =
        list?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
}

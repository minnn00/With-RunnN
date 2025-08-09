package com.with_runn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.with_runn.databinding.FragmentMypageBinding
import com.with_runn.ui.course.TabType
import com.with_runn.data.WalkCourse
import com.with_runn.data.remote.RetrofitInstance
import com.with_runn.data.repository.MyPageRepository
import com.with_runn.data.viewmodel.MyPageViewModel
import com.with_runn.data.viewmodel.MyPageViewModelFactory
import com.with_runn.data.toWalkCourse

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMypageBinding
    private lateinit var adapter: MyPageCourseAdapter
    private lateinit var viewModel: MyPageViewModel

    private val activityVM : ActivityViewModel by activityViewModels()

    private var currentTab = TabType.SCRAP
    private var isDeleteMode = false
    private var isDeleteButtonVisible = false

    private var myCourseList = listOf<WalkCourse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        activityVM.setBottomNavVisibility(true)
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MyPageFragment", "onViewCreated 진입")

        // ViewModel 연결
        val repository = MyPageRepository(
            RetrofitInstance.myPageApi,
            RetrofitInstance.courseApi
        )
        val factory = MyPageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]

        Log.d("MyPageFragment", "ViewModel 생성됨")

        // ✅ 프로필 정보 불러오기
        viewModel.loadUserProfile()

        // 기존 API 호출
        viewModel.loadScrapCourses()
        viewModel.loadLikedCourses()
        Log.d("MyPageFragment", "viewModel.loadScrapCourses() 호출됨")

        // Adapter 설정
        adapter = MyPageCourseAdapter(
            currentTab,
            isDeleteMode,
            onItemDeleteClick = { deletedItem ->
                val newList = myCourseList.toMutableList().apply { remove(deletedItem) }
                myCourseList = newList
                adapter.submitList(newList)
            },
            onItemClicked = { clickedItem ->
                Log.d("MyPageFragment", "Clicked item: ${clickedItem.title}, currentTab=$currentTab, isDeleteMode=$isDeleteMode")
                if (currentTab == TabType.MY_COURSE && !isDeleteButtonVisible && !isDeleteMode) {
                    isDeleteButtonVisible = true
                    isDeleteMode = true
                    binding.btnDeleteMode.visibility = View.VISIBLE
                    adapter.setTabType(currentTab, isDeleteMode)
                    adapter.notifyDataSetChanged()
                }
            },
            onScrapClick = { _ -> /* TODO 서버 삭제 API 연결 */ },
            onLikeClick  = { _ -> /* TODO 서버 삭제 API 연결 */ }
        )

        binding.recyclerMypage.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMypage.adapter = adapter

        // ✅ 프로필 LiveData observe — 모든 필드 바인딩
        viewModel.profile.observe(viewLifecycleOwner) { p ->
            p ?: return@observe
            Log.d("MyPageFragment", "프로필 데이터: $p")

            // 이름
            binding.textName.text = cleanText(p.name)

            // 프로필 이미지
            Glide.with(this).load(p.profileImage).into(binding.imageProfile)

            // 메타(나이 | 견종 | 체급)
            val age  = ageFromBirth(p.birth)                 // "" 또는 "1년 3개월"
            val breed = cleanText(p.breed)                   // ""로 정리
            val size  = cleanText(p.size)                   // "대형견"

            binding.tvMetaAge.text   = age
            binding.tvMetaBreed.text = breed
            binding.tvMetaSize.text  = size

            // 성격/스타일 태그 (서버가 JSON 문자열로 줌)
            val characters = parseTags(p.character)          // ["활발", ...] or []
            val styles     = parseTags(p.style)   // 예: ["사교적","친구 찾기"]

            binding.tvCharMain.text  = characters.firstOrNull() ?: ""
            binding.tvCharMore.text  = if (characters.size > 1) "외 ${characters.size - 1}개" else ""

            binding.tvStyleMain.text = styles.firstOrNull() ?: ""
            binding.tvStyleMore.text = if (styles.size > 1) "외 ${styles.size - 1}개" else ""
        }

        // 기존 리스트 관찰
        viewModel.scrapList.observe(viewLifecycleOwner) { list ->
            if (currentTab == TabType.SCRAP) {
                adapter.submitList(list.map { it.toWalkCourse() })
            }
        }

        viewModel.likeList.observe(viewLifecycleOwner) { list ->
            if (currentTab == TabType.LIKE) {
                adapter.submitList(list.map { it.toWalkCourse() })
            }
        }

        viewModel.myCourseList.observe(viewLifecycleOwner) { list ->
            if (currentTab == TabType.MY_COURSE) {
                val walkCourseList = list.map { it.toWalkCourse() }
                Log.d("MyPageFragment", "받은 코스 개수: ${walkCourseList.size}")
                walkCourseList.forEachIndexed { index, course ->
                    Log.d("MyPageFragment", "[$index] ${course.title}, tags=${course.tags}, image=${course.imageUrl}")
                }
                myCourseList = walkCourseList
                adapter.submitList(walkCourseList)
            }
        }

        setupTabs()
        setupDeleteButtons()

        binding.layoutFollower.setOnClickListener {
            val bundle = Bundle().apply { putInt("initialTab", 0) } // 0: 팔로워
            findNavController().navigate(R.id.action_mypage_graph_to_mypageFollowerFollowFragment, bundle)
        }
        binding.layoutFollowing.setOnClickListener {
            val bundle = Bundle().apply { putInt("initialTab", 1) } // 1: 팔로우
            findNavController().navigate(R.id.action_mypage_graph_to_mypageFollowerFollowFragment, bundle)
        }
    }

    private fun setupTabs() {
        binding.tabScrap.setOnClickListener {
            currentTab = TabType.SCRAP
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            viewModel.loadScrapCourses()
            updateTabUI()
            hideDeleteButtons()
        }

        binding.tabLike.setOnClickListener {
            currentTab = TabType.LIKE
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            viewModel.loadLikedCourses()
            updateTabUI()
            hideDeleteButtons()
        }

        binding.tabMycourses.setOnClickListener {
            currentTab = TabType.MY_COURSE
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            viewModel.loadMyCourses()
            adapter.submitList(myCourseList.toList())
            updateTabUI()
            hideDeleteButtons()
        }
    }

    private fun setupDeleteButtons() {
        binding.btnDeleteMode.setOnClickListener {
            isDeleteMode = true
            adapter.setTabType(currentTab, isDeleteMode)
            binding.btnDeleteMode.visibility = View.GONE
            binding.btnDone.visibility = View.VISIBLE
        }

        binding.btnDone.setOnClickListener {
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            hideDeleteButtons()
        }
    }

    private fun hideDeleteButtons() {
        binding.btnDeleteMode.visibility = View.GONE
        binding.btnDone.visibility = View.GONE
    }

    private fun updateTabUI() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.black)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.gray_400)
        val indicatorOn = ContextCompat.getColor(requireContext(), R.color.green_700)
        val indicatorOff = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        binding.tabScrap.setTextColor(if (currentTab == TabType.SCRAP) selectedColor else unselectedColor)
        binding.tabLike.setTextColor(if (currentTab == TabType.LIKE) selectedColor else unselectedColor)
        binding.tabMycourses.setTextColor(if (currentTab == TabType.MY_COURSE) selectedColor else unselectedColor)

        binding.indicatorScrap.setBackgroundColor(if (currentTab == TabType.SCRAP) indicatorOn else indicatorOff)
        binding.indicatorLike.setBackgroundColor(if (currentTab == TabType.LIKE) indicatorOn else indicatorOff)
        binding.indicatorMycourses.setBackgroundColor(if (currentTab == TabType.MY_COURSE) indicatorOn else indicatorOff)
    }

    // -------------------------
    // 🔽 작은 헬퍼들 (같은 패키지의 Util.kt에 있어도 됨)
    // -------------------------
    private fun moreSuffix(list: List<String>): String {
        val more = (list.size - 1).coerceAtLeast(0)
        return if (more > 0) "외 ${more}개" else ""
    }
}

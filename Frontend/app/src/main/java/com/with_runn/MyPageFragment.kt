package com.with_runn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentMypageBinding

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMypageBinding
    private lateinit var adapter: MyPageCourseAdapter

    private var currentTab = TabType.SCRAP
    private var isDeleteMode = false
    private var isDeleteButtonVisible = false

    private var scrapList = listOf<WalkCourse>()
    private var likeList = listOf<WalkCourse>()
    private var myCourseList = listOf<WalkCourse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        when (currentTab) {
            TabType.SCRAP -> {
                Log.d("MyPageFragment", "갱신: scrap=${CourseStorage.scrapList.size}")
                adapter.setTabType(TabType.SCRAP, isDeleteMode)
                adapter.submitList(CourseStorage.scrapList.toList())
            }
            TabType.LIKE -> {
                Log.d("MyPageFragment", "갱신: like=${CourseStorage.likeList.size}")
                adapter.setTabType(TabType.LIKE, isDeleteMode)
                adapter.submitList(CourseStorage.likeList.toList())
            }
            else->{}
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrapList = CourseStorage.scrapList
        likeList = CourseStorage.likeList


        adapter = MyPageCourseAdapter(
            currentTab,
            isDeleteMode,
            onItemDeleteClick = { deletedItem ->
                val newList = myCourseList.toMutableList().apply {
                    remove(deletedItem)
                }
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
            }
            ,
            onScrapClick = { item ->
                val newList = scrapList.toMutableList().apply {
                    remove(item)
                }
                scrapList = newList
                adapter.submitList(newList)
            },
            onLikeClick = { item ->
                val newList = likeList.toMutableList().apply {
                    remove(item)
                }
                likeList = newList
                adapter.submitList(newList)
            }
        )

        binding.recyclerMypage.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMypage.adapter = adapter
        adapter.submitList(scrapList)

        setupTabs()
        setupDeleteButtons()
    }

    private fun setupTabs() {
        binding.tabScrap.setOnClickListener {
            currentTab = TabType.SCRAP
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            adapter.submitList(CourseStorage.scrapList)
            updateTabUI()
            hideDeleteButtons()
        }

        binding.tabLike.setOnClickListener {
            currentTab = TabType.LIKE
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            adapter.submitList(CourseStorage.likeList)
            updateTabUI()
            hideDeleteButtons()
        }


        binding.tabMycourses.setOnClickListener {
            currentTab = TabType.MY_COURSE
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            adapter.submitList(myCourseList)
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

    private fun getDummyData(): Triple<List<WalkCourse>, List<WalkCourse>, List<WalkCourse>> {
        val scrap = listOf(
            WalkCourse(
                title = "반려견과 한강 산책",
                tags = listOf("탐색활동", "자연친화"),
                imageResId = R.drawable.image,
                distance = "3.2km",
                time = "35분",
                isScrapped = true
            ),
            WalkCourse(
                title = "여의도 벚꽃길",
                tags = listOf("후각자극", "풍경좋음"),
                imageResId = R.drawable.image,
                distance = "2.1km",
                time = "20분",
                isScrapped = true
            )
        )

        val like = listOf(
            WalkCourse(
                title = "남산 자락길",
                tags = listOf("운동효과", "경사로 있음"),
                imageResId = R.drawable.image,
                distance = "4.5km",
                time = "50분",
                isLiked = true
            ),
            WalkCourse(
                title = "성수동 뚝섬 산책로",
                tags = listOf("도심 속 산책", "풍경좋음"),
                imageResId = R.drawable.image,
                distance = "1.8km",
                time = "25분",
                isLiked = true
            )
        )

        val myCourses = listOf(
            WalkCourse(
                title = "우리 집 앞 공원길",
                tags = listOf("짧은 코스", "초보자 추천"),
                imageResId = R.drawable.image,
                distance = "1.2km",
                time = "15분"
            ),
            WalkCourse(
                title = "망원동 강변 산책",
                tags = listOf("탐색활동", "강아지 놀이터"),
                imageResId = R.drawable.image,
                distance = "3.6km",
                time = "40분"
            )
        )

        return Triple(scrap, like, myCourses)
    }
}
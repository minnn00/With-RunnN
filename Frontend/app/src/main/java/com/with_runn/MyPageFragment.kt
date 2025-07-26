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

    // ✅ 항상 최신 데이터를 반영하도록 getter로 처리
    private val scrapList get() = CourseStorage.scrapList
    private val likeList get() = CourseStorage.likeList
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
                Log.d("MyPageFragment", "갱신: scrap=${scrapList.size}")
                adapter.setTabType(TabType.SCRAP, isDeleteMode)
                adapter.submitList(scrapList.toList())
            }
            TabType.LIKE -> {
                Log.d("MyPageFragment", "갱신: like=${likeList.size}")
                adapter.setTabType(TabType.LIKE, isDeleteMode)
                adapter.submitList(likeList.toList())
            }
            else -> {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            },
            onScrapClick = { item ->
                CourseStorage.removeScrap(item)
                adapter.submitList(scrapList.toList())
            },
            onLikeClick = { item ->
                CourseStorage.removeLike(item)
                adapter.submitList(likeList.toList())
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
            adapter.submitList(scrapList.toList())
            updateTabUI()
            hideDeleteButtons()
        }

        binding.tabLike.setOnClickListener {
            currentTab = TabType.LIKE
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            adapter.submitList(likeList.toList())
            updateTabUI()
            hideDeleteButtons()
        }

        binding.tabMycourses.setOnClickListener {
            currentTab = TabType.MY_COURSE
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
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
}

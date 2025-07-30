package com.with_runn.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.with_runn.R
import com.with_runn.ui.adapter.DogCard
import com.with_runn.ui.adapter.DogCardAdapter

class RecommendedFriendsFragment : Fragment() {
    private lateinit var dogCardAdapter: DogCardAdapter
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommended_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 저장된 페이지 상태 복원
        savedInstanceState?.let {
            currentPage = it.getInt("current_page", 0)
        }
        
        setupViewPager()
    }

    private fun setupViewPager() {
        // 4개의 강아지 카드 데이터 생성 (모두 jonny.png 사용!)
        val dogCards = listOf(
            DogCard(
                name = "조니",
                tag = "#에너지 폭발",
                imageResId = R.drawable.jonny,
                tags = listOf("#에너지 폭발", "#강아지 친구 찾기형")
            ),
            DogCard(
                name = "밀리",
                tag = "#호기심 왕성",
                imageResId = R.drawable.jonny,
                tags = listOf("#차분함", "#고집 셈")
            ),
            DogCard(
                name = "호두",
                tag = "#그치만안물어요",
                imageResId = R.drawable.jonny,
                tags = listOf("#낯가림", "#방어적")
            ),
            DogCard(
                name = "루시",
                tag = "#활발함",
                imageResId = R.drawable.jonny,
                tags = listOf("#친화적", "#장난기 많음")
            )
        )

        dogCardAdapter = DogCardAdapter(dogCards)
        val viewPager = view?.findViewById<ViewPager2>(R.id.cardViewpager)
        viewPager?.adapter = dogCardAdapter

        // 저장된 페이지로 이동
        viewPager?.setCurrentItem(currentPage, false)
        
        // 페이지 변경 리스너 추가
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updatePaginationIndicators(position)
            }
        })

        // 카드 클릭 리스너 추가
        dogCardAdapter.setOnItemClickListener { position: Int ->
            val dogCard = dogCards[position]
            val fragment = FriendDetailFragment.newInstance(
                dogName = dogCard.name,
                dogAge = when(position) {
                    0 -> "3년 6개월"
                    1 -> "2년 3개월"
                    2 -> "4년 1개월"
                    3 -> "1년 8개월"
                    else -> "3년 6개월"
                },
                dogBreed = when(position) {
                    0 -> "래브라도 리트리버"
                    1 -> "골든 리트리버"
                    2 -> "허스키"
                    3 -> "보더 콜리"
                    else -> "래브라도 리트리버"
                },
                dogCategory = when(position) {
                    0 -> "대형견"
                    1 -> "대형견"
                    2 -> "중형견"
                    3 -> "중형견"
                    else -> "대형견"
                },
                dogIntro = when(position) {
                    0 -> "안녕하세요! 달리기 좋아하는 3살 조니예요 :)"
                    1 -> "호기심 많은 밀리입니다! 새로운 친구 만나고 싶어요~"
                    2 -> "조용한 호두입니다. 천천히 친해져요 :)"
                    3 -> "활발한 루시예요! 함께 놀아요!"
                    else -> "안녕하세요! 달리기 좋아하는 3살 조니예요 :)"
                }
            )
            
            // Fragment 교체
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("current_page", currentPage)
    }
    
    private fun updatePaginationIndicators(selectedPosition: Int) {
        val indicator0 = view?.findViewById<ImageView>(R.id.indicator_0)
        val indicator1 = view?.findViewById<ImageView>(R.id.indicator_1)
        val indicator2 = view?.findViewById<ImageView>(R.id.indicator_2)
        val indicator3 = view?.findViewById<ImageView>(R.id.indicator_3)
        val indicators = listOf(indicator0, indicator1, indicator2, indicator3)
        
        indicators.forEachIndexed { idx, imageView ->
            imageView?.setImageResource(
                if (idx == selectedPosition) R.drawable.ic_indicator_active
                else R.drawable.ic_indicator_inactive
            )
        }
    }
} 
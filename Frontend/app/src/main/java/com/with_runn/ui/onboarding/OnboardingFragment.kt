package com.with_runn.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val dots = mutableListOf<ImageView>()
    private val NUM_PAGES = 3

    private val title = listOf(
        "우리 동네\n산책메이트 찾기",
        "산책코스 추천",
        "내 주변 반려 동물 시설"
    )

    private val description = listOf(
        "함께 산책할 반려견을 찾아보아요!",
        "내가 만든 산책 코스를\n산책 메이트와 공유해요",
        "병원, 미용 시설 뿐만 아니라\n반려동물과 함께 입장할 수 있는 시설을 알려줄게요"
    )

    private val images = listOf(
        R.drawable.img_onboarding1,
        R.drawable.img_onboarding2,
        R.drawable.img_onboarding3
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = binding.viewPager2
        val dotLayout = binding.dotLayout
        val nextButton = binding.btnNext

        viewPager.adapter = OnboardingPagerAdapter(this, title, description, images)

        setupDots(dotLayout)
        setCurrentDot(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentDot(position)
                updateNextButtonText(position)
            }
        })

        nextButton.setOnClickListener {
            val current = viewPager.currentItem
            if (current < NUM_PAGES - 1) {
                viewPager.currentItem = current + 1
            } else {
                findNavController().navigate(R.id.action_onboardingFragment_to_onboardingLoginFragment)
            }
        }
    }

    private fun updateNextButtonText(position: Int) {
        binding.btnNext.text = if (position == NUM_PAGES - 1) "시작하기" else "다음"
    }

    private fun setupDots(dotLayout: LinearLayout) {
        dots.clear()
        dotLayout.removeAllViews()

        for (i in 0 until NUM_PAGES) {
            val dot = ImageView(requireContext()).apply {
                setImageResource(R.drawable.ic_dot_inactive)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(10, 0, 10, 0)
                }
                layoutParams = params
            }
            dots.add(dot)
            dotLayout.addView(dot)
        }
    }

    private fun setCurrentDot(index: Int) {
        for (i in dots.indices) {
            dots[i].setImageResource(
                if (i == index) R.drawable.ic_dot_active
                else R.drawable.ic_dot_inactive
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

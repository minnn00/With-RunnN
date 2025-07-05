package com.with_runn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
//온보딩 종료 후 이동할 액티비티
import com.with_runn.ui.onboarding.OnboardingLoginActivity
import com.with_runn.R

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotLayout: LinearLayout
    private lateinit var nextButton: Button
    private val dots = mutableListOf<ImageView>()
    private val NUM_PAGES = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager2)
        dotLayout = findViewById(R.id.dotLayout)
        nextButton = findViewById(R.id.btn_next)

        viewPager.adapter = OnboardingPagerAdapter(this)
        setupDots()
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
                // 마지막 페이지: 다른 액티비티로 이동하거나 종료
                val intent = Intent(this, OnboardingLoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateNextButtonText(position: Int) {
        nextButton.text = if (position == NUM_PAGES - 1) "시작하기" else "다음"
    }

    private fun setupDots() {
        for (i in 0 until NUM_PAGES) {
            val dot = ImageView(this).apply {
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
        for (i in 0 until NUM_PAGES) {
            dots[i].setImageResource(
                if (i == index) R.drawable.ic_dot_active
                else R.drawable.ic_dot_inactive
            )
        }
    }
}

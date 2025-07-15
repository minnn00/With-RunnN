package com.with_runn.ui.onboarding
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.with_runn.R
import com.with_runn.databinding.ActivityOnboardingProfileWalkingstyleBinding

class OnboardingProfileWalkingStyleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingProfileWalkingstyleBinding

    private lateinit var notice_text: TextView
    private lateinit var chipGroup: ChipGroup

    val items = listOf("빠른 산책","느긋한 산책","속도균형",
        "냄새 탐험","앞서 걷는 타입",
        "보호자 옆 걷기","보호자보다 뒤처지는 타입",
        "강아지 친구 찾기형","사람 친화형",
        "보호자 밀착형","주변 경계형",
        "직접 입력")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingProfileWalkingstyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notice_text = binding.noticeText
        chipGroup = binding.chipgroup

        val textData: String = notice_text.text.toString()

        val builder = SpannableStringBuilder(textData)

        val colorGreenSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.green_700))
        builder.setSpan(colorGreenSpan, 3, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        notice_text.text = builder

        val inflater = LayoutInflater.from(ContextThemeWrapper(this, R.style.onboarding_chip))
        for (item in items) {
            val chip = inflater.inflate(R.layout.view_onboarding_chip, chipGroup, false) as Chip
            chip.text = item
            chip.isCheckable = true
            chip.isClickable = true
            chipGroup.addView(chip)
        }

    }
}
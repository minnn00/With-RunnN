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
import com.with_runn.databinding.ActivityOnboardingProfilePersonalityBinding

class OnboardingProfilePersonalityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingProfilePersonalityBinding

    private lateinit var notice_text: TextView
    private lateinit var chipGroup: ChipGroup

    val items = listOf("활발함", "차분함", "에너지 폭발",
        "느긋함", "사교적", "낯가림",
        "의존적", "겁쟁이", "낯가림",
        "고집 셈", "보호자 중심", "호기심 왕성",
        "독립적", "조용함", "장난꾸러기",
        "방어적", "스킨십 좋아함",
        "스킨십 싫어함", "직접 입력")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingProfilePersonalityBinding.inflate(layoutInflater)
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

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
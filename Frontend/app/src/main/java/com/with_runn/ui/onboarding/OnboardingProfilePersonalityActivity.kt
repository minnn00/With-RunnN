package com.with_runn.ui.onboarding
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.with_runn.R
import com.with_runn.databinding.ActivityOnboardingProfilePersonalityBinding

class OnboardingProfilePersonalityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingProfilePersonalityBinding

    private lateinit var notice_text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingProfilePersonalityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. TextView 참조
        notice_text = binding.noticeText

        // 2. String 문자열 데이터 취득
        val textData: String = notice_text.text.toString()

        // 3. SpannableStringBuilder 타입으로 변환
        val builder = SpannableStringBuilder(textData)

        // 4-1. index=0 에 해당하는 문자열(0)에 볼드체적용
        val colorGreenSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.green_700))
        builder.setSpan(colorGreenSpan, 3, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        // 5. TextView에 적용
        notice_text.text = builder

    }
}
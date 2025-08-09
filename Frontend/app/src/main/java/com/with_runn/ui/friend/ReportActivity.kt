package com.with_runn.ui.friend

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.with_runn.R
import com.with_runn.databinding.ActivityReportBinding

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 시스템 UI 설정
        setupSystemUI()

        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupTextWatcher()
    }

    private fun setupSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.isAppearanceLightNavigationBars = true
    }

    private fun setupViews() {
        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            finish()
        }

        // 제출하기 버튼 (초기에는 비활성화)
        binding.submitButton.setOnClickListener {
            // 신고 제출 로직 구현
            // TODO: 실제 신고 제출 로직 구현
            finish()
        }
    }

    private fun setupTextWatcher() {
        binding.reportEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val textLength = text.length

                // 글자 수 업데이트
                binding.characterCount.text = "$textLength/300"

                // 텍스트 필드 스타일 변경
                if (text.isNotEmpty()) {
                    // 활성화 상태
                    binding.reportEditText.setBackgroundResource(R.drawable.bg_entry_active)
                    binding.characterCount.setTextColor(resources.getColor(R.color.green_700, null))
                    binding.submitButton.isEnabled = true
                    binding.submitButton.setBackgroundResource(R.drawable.rounded_button_green)
                } else {
                    // 비활성화 상태
                    binding.reportEditText.setBackgroundResource(R.drawable.bg_entry_inactive)
                    binding.characterCount.setTextColor(resources.getColor(R.color.gray_400, null))
                    binding.submitButton.isEnabled = false
                    binding.submitButton.setBackgroundResource(R.drawable.bg_button_inactive)
                }
            }
        })
    }
} 
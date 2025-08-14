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
import androidx.lifecycle.ViewModelProvider
import com.with_runn.R
import com.with_runn.databinding.ActivityReportBinding
import com.with_runn.ui.friend.viewmodel.RecommendedFriendViewModel

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private lateinit var viewModel: RecommendedFriendViewModel
    private var reportedUserId: Int = 0

    companion object {
        const val EXTRA_REPORTED_USER_ID = "reported_user_id"
        const val EXTRA_REPORTED_USER_NAME = "reported_user_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 시스템 UI 설정
        setupSystemUI()

        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this)[RecommendedFriendViewModel::class.java]

        // 신고 대상 정보 수신
        reportedUserId = intent.getIntExtra(EXTRA_REPORTED_USER_ID, 0)
        val reportedUserName = intent.getStringExtra(EXTRA_REPORTED_USER_NAME) ?: ""

        // 상단 타이틀 등에 대상 표시 (있다면)
        if (reportedUserName.isNotEmpty()) {
            // 필요 시 타이틀 텍스트 업데이트 등
        }

        setupViews()
        setupTextWatcher()
        setupObservers()
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
            val reason = binding.reportEditText.text?.toString()?.trim() ?: ""
            if (reportedUserId <= 0) {
                android.widget.Toast.makeText(this, "신고 대상을 확인할 수 없습니다.", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (reason.isEmpty()) {
                android.widget.Toast.makeText(this, "신고 사유를 입력해주세요.", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 신고 API 호출
            viewModel.reportFriend(reportedUserId, reason)
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

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.submitButton.isEnabled = !isLoading && (binding.reportEditText.text?.isNotEmpty() == true)
        }

        viewModel.reportResult.observe(this) { result ->
            result?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
                viewModel.clearReportResult()
                finish()
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
} 
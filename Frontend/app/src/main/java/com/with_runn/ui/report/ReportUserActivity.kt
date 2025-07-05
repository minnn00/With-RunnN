package com.with_runn.ui.report

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.with_runn.R
import com.with_runn.databinding.ActivityReportBinding

class ReportUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private var friendId: String? = null
    private var friendName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupToolbar()
        setupViews()
        setupTextWatcher()
    }

    private fun getIntentData() {
        friendId = intent.getStringExtra("friend_id")
        friendName = intent.getStringExtra("friend_name")
        
        Log.d("ReportUserActivity", "신고 대상: $friendName (ID: $friendId)")
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "신고하기"
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupViews() {
        // 제출 버튼 초기 상태 설정
        updateSubmitButton(false)
        
        // 제출 버튼 클릭 리스너
        binding.btnSubmit.setOnClickListener {
            submitReport()
        }
    }

    private fun setupTextWatcher() {
        binding.etReportReason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 구현 필요 없음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 구현 필요 없음
            }

            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrEmpty() && s.toString().trim().isNotEmpty()
                updateSubmitButton(hasText)
            }
        })
    }

    private fun updateSubmitButton(enabled: Boolean) {
        binding.btnSubmit.apply {
            isEnabled = enabled
            backgroundTintList = if (enabled) {
                ContextCompat.getColorStateList(this@ReportUserActivity, R.color.green_700)
            } else {
                ContextCompat.getColorStateList(this@ReportUserActivity, R.color.gray_400)
            }
        }
    }

    private fun submitReport() {
        val reportReason = binding.etReportReason.text.toString().trim()
        
        if (reportReason.isEmpty()) {
            return
        }

        // 실제 구현에서는 서버에 신고 데이터 전송
        Log.d("ReportUserActivity", "신고 제출 - 대상: $friendName, 사유: $reportReason")
        
        // TODO: 실제 API 호출
        // reportAPI.submitReport(friendId, reportReason) { success ->
        //     if (success) {
        //         showReportCompleteDialog()
        //     } else {
        //         showReportFailDialog()
        //     }
        // }
        
        // 임시로 로그만 출력하고 화면 종료
        Log.d("ReportUserActivity", "신고가 접수되었습니다.")
        finish()
    }
} 
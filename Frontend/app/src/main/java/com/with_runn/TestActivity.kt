package com.with_runn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.with_runn.databinding.ActivityTestBinding
import com.with_runn.ui.friends.FriendsActivity

class TestActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTestBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.apply {
            // 친구 기능 테스트 버튼
            btnTestFriends.setOnClickListener {
                val intent = Intent(this@TestActivity, FriendsActivity::class.java)
                startActivity(intent)
            }
            
            // 기존 MainActivity로 이동
            btnMainActivity.setOnClickListener {
                val intent = Intent(this@TestActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
} 
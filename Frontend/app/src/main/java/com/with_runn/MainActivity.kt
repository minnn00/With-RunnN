package com.with_runn

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.with_runn.databinding.ActivityMainBinding
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 하단 네비게이션 바 강제 표시
        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.bottomNavigationView.bringToFront()

        // NavController 설정
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Edge-to-Edge 모드 처리
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bottomNavigationView.setPadding(0, 0, 0, insets.bottom)
            
            // 네비게이션 바가 보이지 않을 경우 강제로 표시
            if (binding.bottomNavigationView.visibility != View.VISIBLE) {
                binding.bottomNavigationView.visibility = View.VISIBLE
                binding.bottomNavigationView.bringToFront()
            }
            
            WindowInsetsCompat.CONSUMED
        }

        // 추가 안전장치: 지연 후 네비게이션 바 확인
        binding.root.post {
            if (binding.bottomNavigationView.visibility != View.VISIBLE) {
                binding.bottomNavigationView.visibility = View.VISIBLE
                binding.bottomNavigationView.bringToFront()
            }
        }
    }
}
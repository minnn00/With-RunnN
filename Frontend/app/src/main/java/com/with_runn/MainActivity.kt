package com.with_runn

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.with_runn.databinding.ActivityMainBinding
import com.with_runn.ui.friend.DogCardMainActivity
import kotlin.jvm.java
import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.combine
import com.google.android.material.snackbar.Snackbar
import com.with_runn.ui.onboarding.OnboardingActivity
import com.with_runn.data.TokenManager
import com.with_runn.ui.chat.activity.ChatActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private val activityVM : ActivityViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "위치 권한 허용됨")
        } else {
            Log.d("MainActivity", "위치 권한 거부됨")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        routeIfUnauthenticated()
    }

    private fun checkAndRequestLocationPermission() {
        val isGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun routeIfUnauthenticated() {
        lifecycleScope.launch {
            val token = TokenManager.getAccessToken()
            val memberId = TokenManager.getCurrentUserId()

            val isInvalid = token.isNullOrBlank() || memberId == -1
            if (isInvalid) {
                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                finish()
                return@launch
            }

            activityVM.loadToken()
            // 토큰 있으면 정상 UI 세팅
            setupUi()
        }
    }

    private fun setupUi() {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        lifecycleScope.launch {
            activityVM.isBottomNavVisible.collect { isVisible ->
                if (isVisible) binding.bottomNavigationView.slideUp()
                else binding.bottomNavigationView.slideDown()
            }
        }
        lifecycleScope.launch {
            activityVM.isToolBarVisible.collect { isVisible ->
                binding.upperToolBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    activityVM.firstRegion, activityVM.secondRegion, activityVM.thirdRegion
                ) { f, s, t ->
                    listOfNotNull(f?.name, s?.name, t?.name)
                        .joinToString(" ").ifEmpty { "지역 미설정" }
                }.collect { text -> binding.regionText.text = text }
            }
        }
        
        binding.apply {
            setLocationBtn.setOnClickListener { navController.navigate(R.id.locationSetFragment) }
            alarmBtn.setOnClickListener { navController.navigate(R.id.locationSetFragment) } // TODO: 알람 화면
            chatBtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, ChatActivity::class.java))
            }
        }

        activityVM.loadToken()

        checkAndRequestLocationPermission()
    }

    fun showSnackbar(
        message: String,
        actionText: String? = null,
        anchorView: View? = null,
        duration: Int = Snackbar.LENGTH_LONG,
        action: (() -> Unit)? = null
    ) {
        val parent = findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(parent, message, duration)

        if (anchorView != null) snackbar.setAnchorView(anchorView)
        if (actionText != null && action != null) {
            snackbar.setAction(actionText) { action() }
        }

        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_950))

        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.gray_050))
        textView.typeface = ResourcesCompat.getFont(this, R.font.pretendard_medium)

        val actionView = snackbar.view.findViewById<Button>(com.google.android.material.R.id.snackbar_action)
        actionView.setTextColor(ContextCompat.getColor(this, R.color.green_700))
        actionView.typeface = ResourcesCompat.getFont(this, R.font.pretendard_medium)

        snackbar.show()
    }
}
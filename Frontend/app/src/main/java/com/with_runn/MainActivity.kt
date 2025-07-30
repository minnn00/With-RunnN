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
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.places.api.Places
import com.google.android.material.snackbar.Snackbar
import com.with_runn.ui.onboarding.OnboardingActivity
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

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment;
        val navController = navHost.navController;

        binding.bottomNavigationView.setupWithNavController(navController)

        lifecycleScope.launch {
            activityVM.isBottomNavVisible.collect { isBottomNavVisible ->
                if(isBottomNavVisible){
                    //binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.bottomNavigationView.slideUp()
                }else{
                    //binding.bottomNavigationView.visibility = View.GONE
                    binding.bottomNavigationView.slideDown()
                }

            }
        }

        checkAndRequestLocationPermission()
        Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.GOOGLE_MAP_API_KEY)

//        val intent = Intent(this, OnboardingActivity::class.java)
//        startActivity(intent)
    }

    private fun checkAndRequestLocationPermission() {
        val isGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
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
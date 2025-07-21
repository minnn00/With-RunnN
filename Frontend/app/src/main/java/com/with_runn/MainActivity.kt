package com.with_runn

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.with_runn.databinding.ActivityMainBinding

@RequiresApi(Build.VERSION_CODES.P)
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment;
        val navController = navHost.navController;

        binding.bottomNavigationView.setupWithNavController(navController)
    }
}
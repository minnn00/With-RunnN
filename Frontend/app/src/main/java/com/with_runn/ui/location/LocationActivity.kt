package com.with_runn.ui.location

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.with_runn.R
import com.with_runn.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationBinding
    
    private lateinit var provinceAdapter: LocationAdapter
    private lateinit var cityAdapter: LocationAdapter
    private lateinit var districtAdapter: LocationAdapter
    
    private var selectedProvince: String? = null
    private var selectedCity: String? = null
    private var selectedDistrict: String? = null
    
    private val provinces = listOf(
        "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", 
        "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원도", 
        "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"
    )
    
    private val cities = mapOf(
        "서울특별시" to listOf("강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"),
        "경기도" to listOf("수원시", "성남시", "고양시", "용인시", "부천시", "안산시", "안양시", "남양주시", "화성시", "평택시", "의정부시", "시흥시", "파주시", "광명시", "김포시", "군포시", "이천시", "양주시", "오산시", "구리시", "안성시", "포천시", "의왕시", "하남시", "여주시", "동두천시", "과천시", "가평군", "양평군", "연천군"),
        "부산광역시" to listOf("중구", "서구", "동구", "영도구", "부산진구", "동래구", "남구", "북구", "해운대구", "사하구", "금정구", "강서구", "연제구", "수영구", "사상구", "기장군")
    )
    
    private val districts = mapOf(
        "마포구" to listOf("공덕동", "용강동", "대흥동", "염리동", "신수동", "서강동", "서교동", "합정동", "망원동", "연남동", "성산동", "상암동"),
        "강남구" to listOf("신사동", "논현동", "압구정동", "청담동", "삼성동", "대치동", "역삼동", "개포동", "세곡동", "자곡동", "율현동", "일원동", "수서동"),
        "서초구" to listOf("서초동", "잠원동", "반포동", "방배동", "양재동", "우면동", "원지동", "내곡동")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupViews()
        setupRecyclerViews()
        setupClickListeners()
        loadProvinces()
    }

    private fun setupViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupRecyclerViews() {
        // 시/도 RecyclerView
        provinceAdapter = LocationAdapter { locationName ->
            selectedProvince = locationName
            selectedCity = null
            selectedDistrict = null
            
            updateSelectedLocation()
            loadCities(locationName)
            clearDistricts()
        }
        
        binding.rvProvince.apply {
            layoutManager = LinearLayoutManager(this@LocationActivity)
            adapter = provinceAdapter
        }
        
        // 시/군/구 RecyclerView
        cityAdapter = LocationAdapter { locationName ->
            selectedCity = locationName
            selectedDistrict = null
            
            updateSelectedLocation()
            loadDistricts(locationName)
        }
        
        binding.rvCity.apply {
            layoutManager = LinearLayoutManager(this@LocationActivity)
            adapter = cityAdapter
        }
        
        // 동/읍/면 RecyclerView
        districtAdapter = LocationAdapter { locationName ->
            selectedDistrict = locationName
            updateSelectedLocation()
        }
        
        binding.rvDistrict.apply {
            layoutManager = LinearLayoutManager(this@LocationActivity)
            adapter = districtAdapter
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        binding.btnSave.setOnClickListener {
            saveLocation()
        }
    }

    private fun loadProvinces() {
        provinceAdapter.submitList(provinces)
    }

    private fun loadCities(province: String) {
        val cityList = cities[province] ?: emptyList()
        cityAdapter.submitList(cityList)
    }

    private fun loadDistricts(city: String) {
        val districtList = districts[city] ?: emptyList()
        districtAdapter.submitList(districtList)
    }

    private fun clearDistricts() {
        districtAdapter.submitList(emptyList())
    }

    private fun updateSelectedLocation() {
        binding.chipGroupLocation.removeAllViews()
        
        val selectedLocations = listOfNotNull(selectedProvince, selectedCity, selectedDistrict)
        
        selectedLocations.forEach { location ->
            val chip = Chip(this).apply {
                text = location
                isCloseIconVisible = false
                isCheckable = false
                isClickable = false
                setChipBackgroundColorResource(R.color.green_100)
                setTextColor(getColor(R.color.green_700))
            }
            binding.chipGroupLocation.addView(chip)
        }
    }

    private fun saveLocation() {
        val resultIntent = Intent().apply {
            putExtra("province", selectedProvince)
            putExtra("city", selectedCity)
            putExtra("district", selectedDistrict)
        }
        
        setResult(Activity.RESULT_OK, resultIntent)
        
        Log.d("LocationActivity", "저장된 지역: $selectedProvince $selectedCity $selectedDistrict")
        finish()
    }
} 
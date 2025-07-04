package com.with_runn

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LocationSettingActivity : AppCompatActivity() {

    private lateinit var selectedLocationText: TextView
    private lateinit var cityRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_setting)

        selectedLocationText = findViewById(R.id.selected_location_text)
        cityRecyclerView = findViewById(R.id.recycler_sido)

        val cities = listOf("서울", "경기", "부산", "대구", "인천", "광주", "대전", "울산")

        val adapter = CityAdapter(cities) { selectedCity ->
            selectedLocationText.text = "선택지역: $selectedCity"
        }

        cityRecyclerView.layoutManager = LinearLayoutManager(this)
        cityRecyclerView.adapter = adapter
    }
}
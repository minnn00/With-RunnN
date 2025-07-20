package com.with_runn.ui.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.with_runn.R
import com.with_runn.databinding.ActivityOnboardingProfileDefultBinding
import kotlin.math.log

class OnboardingProfileDefultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingProfileDefultBinding

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var name: String
    private var sex: String = "남"
    private lateinit var birthday: String
    private lateinit var breed: String
    private var size: String = "소형견"

    private var name_saveable: Int = 0 //0: 중복 확인 안함, 1: 이름 중복 오류, 2: 사용 가능
    private var breed_savable: Boolean = false //false: 입력x 혹은 15글자 초과, true: 사용 가능

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnboardingProfileDefultBinding.inflate(layoutInflater);
        setContentView(binding.root);

        preferences = getSharedPreferences("user_info", MODE_PRIVATE)
        editor = preferences.edit()

        binding.nameEditText.setText(preferences.getString("name","none"))
        binding.birthdayEditText.setText(preferences.getString("birthday","YYYY/MM/DD"))
        binding.breedEditText.setText(preferences.getString("breed","15자 이내로 입력해주세요"))
        if (preferences.contains("breed")) breed_savable = true

        when (preferences.getString("sex","남")) {
            "남" -> {
                binding.buttonMale.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_active)
                binding.buttonFemale.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_inactive)
                binding.buttonMale.setTextColor(ContextCompat.getColor(this,R.color.green_700))
                binding.buttonFemale.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
            }
            "여" -> {
                binding.buttonFemale.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_active)
                binding.buttonMale.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_inactive)
                binding.buttonFemale.setTextColor(ContextCompat.getColor(this,R.color.green_700))
                binding.buttonMale.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
            }
        }
        when (preferences.getString("size","소형견")) {
            "소형견" -> {
                binding.buttonSmall.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_active)
                binding.buttonMiddle.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_inactive)
                binding.buttonBig.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_inactive)
                binding.buttonSmall.setTextColor(ContextCompat.getColor(this,R.color.green_700))
                binding.buttonMiddle.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
                binding.buttonBig.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
            }
            "중형견" -> {
                binding.buttonMiddle.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_active)
                binding.buttonSmall.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_inactive)
                binding.buttonBig.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_inactive)
                binding.buttonMiddle.setTextColor(ContextCompat.getColor(this,R.color.green_700))
                binding.buttonSmall.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
                binding.buttonBig.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
            }
            "대형견" -> {
                binding.buttonBig.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_active)
                binding.buttonMiddle.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_inactive)
                binding.buttonSmall.background = ContextCompat.getDrawable(this, R.drawable.bg_switch_inactive)
                binding.buttonBig.setTextColor(ContextCompat.getColor(this,R.color.green_700))
                binding.buttonMiddle.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
                binding.buttonSmall.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
            }

        }

        //저장 버튼
        binding.saveButton.setOnClickListener {
            name = binding.nameEditText.text.toString()
            birthday = binding.birthdayEditText.text.toString()
            breed = binding.breedEditText.text.toString()

            if (name_saveable==0){
                binding.entryName.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_error)
                binding.errorImg.visibility = View.VISIBLE
                binding.nameLogText.text = "중복 확인을 해주세요"
                binding.nameLogText.setTextColor(ContextCompat.getColor(this,R.color.red_500))
                binding.nameLog.visibility = View.VISIBLE
            }

            else if (name_saveable==2 && breed_savable) {
                editor.putString("name", name)
                editor.putString("sex", sex)
                editor.putString("birthday", birthday)
                editor.putString("breed", breed)
                editor.putString("size", size)
                editor.apply()

                val intent = Intent(this, OnboardingProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        // 성별: 버튼으로 일단 구현 (라디오버튼처럼)
        binding.buttonMale.setOnClickListener {
            sex = "남"
            binding.buttonFemale.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_inactive)
            binding.buttonMale.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_active)
            binding.buttonMale.setTextColor(ContextCompat.getColor(this,R.color.green_700))
            binding.buttonFemale.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
        }
        binding.buttonFemale.setOnClickListener {
            sex = "여"
            binding.buttonFemale.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_active)
            binding.buttonMale.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_inactive)
            binding.buttonFemale.setTextColor(ContextCompat.getColor(this,R.color.green_700))
            binding.buttonMale.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
        }
        // 크기: chip 스타일이 적용이 안돼서 일단 버튼으로 구현
        binding.buttonSmall.setOnClickListener {
            size = "소형견"
            binding.buttonMiddle.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_inactive)
            binding.buttonBig.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_inactive)
            binding.buttonSmall.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_active)

            binding.buttonSmall.setTextColor(ContextCompat.getColor(this,R.color.green_700))
            binding.buttonMiddle.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
            binding.buttonBig.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
        }
        binding.buttonMiddle.setOnClickListener {
            size = "중형견"
            binding.buttonSmall.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_inactive)
            binding.buttonBig.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_inactive)
            binding.buttonMiddle.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_active)

            binding.buttonMiddle.setTextColor(ContextCompat.getColor(this,R.color.green_700))
            binding.buttonSmall.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
            binding.buttonBig.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
        }
        binding.buttonBig.setOnClickListener {
            size = "대형견"
            binding.buttonMiddle.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_inactive)
            binding.buttonSmall.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_inactive)
            binding.buttonBig.background = ContextCompat.getDrawable(this,R.drawable.bg_switch_active)

            binding.buttonBig.setTextColor(ContextCompat.getColor(this,R.color.green_700))
            binding.buttonMiddle.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
            binding.buttonSmall.setTextColor(ContextCompat.getColor(this,R.color.gray_500))
        }
        binding.nameCheckButton.setOnClickListener {
            Log.d("name", binding.nameEditText.text.toString() + " " + preferences.getString("name","none"))
            if (binding.nameEditText.text.toString() == preferences.getString("name","none")) {
                binding.entryName.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_error)
                binding.errorImg.visibility = View.VISIBLE
                binding.nameLogText.text = "중복된 이름입니다"
                binding.nameLogText.setTextColor(ContextCompat.getColor(this,R.color.red_500))
                binding.nameLog.visibility = View.VISIBLE
                name_saveable = 1
            }
            else {
                binding.entryName.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_active)
                binding.errorImg.visibility = View.GONE
                binding.nameLogText.text = "사용 가능한 이름입니다"
                binding.nameLogText.setTextColor(ContextCompat.getColor(this,R.color.green_700))
                binding.nameLog.visibility = View.VISIBLE
                name_saveable = 2
            }
        }

        binding.nameEditText.doAfterTextChanged {
            name_saveable = 0
            if (binding.nameEditText.text.toString().length < 2){
                binding.entryName.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_error)
                binding.errorImg.visibility = View.VISIBLE
                binding.nameLogText.text = "최소 두 글자 이상 작성해주세요"
                binding.nameLogText.setTextColor(ContextCompat.getColor(this,R.color.red_500))
                binding.nameLog.visibility = View.VISIBLE
            }
            else{
                binding.entryName.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_inactive)
                binding.errorImg.visibility = View.GONE
                binding.nameLog.visibility = View.INVISIBLE
            }
        }

        binding.birthdayEditText.setOnClickListener {
            binding.birthdayEditText.setText("")
//            val datePicker = DatePickerFragment()
//            datePicker.show(supportFragmentManager, "datePicker")
            //todo: datePicker 구현
        }
        binding.breedEditText.setOnClickListener {
            binding.breedEditText.setText("")
        }
        binding.breedEditText.doAfterTextChanged {
            if (binding.breedEditText.text.toString().length > 15){
                binding.entryBreed.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_error)
                binding.breedLog.visibility = View.VISIBLE
                breed_savable = false
            }
            else{
                binding.entryBreed.background = ContextCompat.getDrawable(this,R.drawable.bg_entry_inactive)
                binding.breedLog.visibility = View.INVISIBLE
                breed_savable = true
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
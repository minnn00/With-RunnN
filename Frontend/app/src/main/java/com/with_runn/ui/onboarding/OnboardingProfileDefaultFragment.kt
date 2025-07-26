package com.with_runn.ui.onboarding

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboardingProfileDefaultBinding

class OnboardingProfileDefaultFragment : Fragment() {

    private var _binding: FragmentOnboardingProfileDefaultBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var name: String
    private var sex: String = "남"
    private lateinit var birthday: String
    private lateinit var breed: String
    private var size: String = "소형견"

    private var name_saveable: Int = 0
    private var breed_savable: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingProfileDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        editor = preferences.edit()

        binding.nameEditText.setText(preferences.getString("name", "none"))
        binding.birthdayEditText.setText(preferences.getString("birthday", "YYYY/MM/DD"))
        binding.breedEditText.setText(preferences.getString("breed", "15자 이내로 입력해주세요"))
        if (preferences.contains("breed")) breed_savable = true

        // 초기 UI 설정
        setInitialSex(preferences.getString("sex", "남")!!)
        setInitialSize(preferences.getString("size", "소형견")!!)

        // 저장 버튼 클릭
        binding.saveButton.setOnClickListener {
            name = binding.nameEditText.text.toString()
            birthday = binding.birthdayEditText.text.toString()
            breed = binding.breedEditText.text.toString()

            if (name_saveable == 0) {
                showNameError("중복 확인을 해주세요")
            } else if (name_saveable == 2 && breed_savable) {
                editor.putString("name", name)
                editor.putString("sex", sex)
                editor.putString("birthday", birthday)
                editor.putString("breed", breed)
                editor.putString("size", size)
                editor.apply()

                findNavController().popBackStack() // 프로필 프래그먼트로 복귀
            }
        }

        // 성별 버튼 처리
        binding.buttonMale.setOnClickListener { setSex("남") }
        binding.buttonFemale.setOnClickListener { setSex("여") }

        // 크기 버튼 처리
        binding.buttonSmall.setOnClickListener { setSize("소형견") }
        binding.buttonMiddle.setOnClickListener { setSize("중형견") }
        binding.buttonBig.setOnClickListener { setSize("대형견") }

        // 이름 중복 확인
        binding.nameCheckButton.setOnClickListener {
            val currentName = binding.nameEditText.text.toString()
            val savedName = preferences.getString("name", "none")

            if (currentName == savedName) {
                showNameError("중복된 이름입니다")
                name_saveable = 1
            } else {
                showNameAvailable()
                name_saveable = 2
            }
        }

        binding.nameEditText.doAfterTextChanged {
            name_saveable = 0
            if (binding.nameEditText.text.toString().length < 2) {
                showNameError("최소 두 글자 이상 작성해주세요")
            } else {
                binding.entryName.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_inactive)
                binding.errorImg.visibility = View.GONE
                binding.nameLog.visibility = View.INVISIBLE
            }
        }

        binding.birthdayEditText.setOnClickListener {
            binding.birthdayEditText.setText("")
            // TODO: DatePicker Fragment 연동
        }

        binding.breedEditText.setOnClickListener {
            binding.breedEditText.setText("")
        }

        binding.breedEditText.doAfterTextChanged {
            val input = binding.breedEditText.text.toString()
            if (input.length > 15) {
                binding.entryBreed.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_error)
                binding.breedLog.visibility = View.VISIBLE
                breed_savable = false
            } else {
                binding.entryBreed.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_inactive)
                binding.breedLog.visibility = View.INVISIBLE
                breed_savable = true
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showNameError(message: String) {
        binding.entryName.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_error)
        binding.errorImg.visibility = View.VISIBLE
        binding.nameLogText.text = message
        binding.nameLogText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_500))
        binding.nameLog.visibility = View.VISIBLE
    }

    private fun showNameAvailable() {
        binding.entryName.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_active)
        binding.errorImg.visibility = View.GONE
        binding.nameLogText.text = "사용 가능한 이름입니다"
        binding.nameLogText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_700))
        binding.nameLog.visibility = View.VISIBLE
    }

    private fun setSex(selected: String) {
        sex = selected
        val isMale = selected == "남"
        binding.buttonMale.apply {
            background = ContextCompat.getDrawable(requireContext(),
                if (isMale) R.drawable.bg_switch_active else R.drawable.bg_switch_inactive)
            setTextColor(ContextCompat.getColor(requireContext(),
                if (isMale) R.color.green_700 else R.color.gray_500))
        }
        binding.buttonFemale.apply {
            background = ContextCompat.getDrawable(requireContext(),
                if (!isMale) R.drawable.bg_switch_active else R.drawable.bg_switch_inactive)
            setTextColor(ContextCompat.getColor(requireContext(),
                if (!isMale) R.color.green_700 else R.color.gray_500))
        }
    }

    private fun setSize(selected: String) {
        size = selected
        val colors = listOf(
            binding.buttonSmall to "소형견",
            binding.buttonMiddle to "중형견",
            binding.buttonBig to "대형견"
        )
        for ((button, label) in colors) {
            button.background = ContextCompat.getDrawable(
                requireContext(),
                if (label == selected) R.drawable.bg_switch_active else R.drawable.bg_switch_inactive
            )
            button.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (label == selected) R.color.green_700 else R.color.gray_500
            ))
        }
    }

    private fun setInitialSex(sexValue: String) {
        setSex(sexValue)
    }

    private fun setInitialSize(sizeValue: String) {
        setSize(sizeValue)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

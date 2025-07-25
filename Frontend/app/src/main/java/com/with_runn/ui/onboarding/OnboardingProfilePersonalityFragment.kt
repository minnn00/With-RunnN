package com.with_runn.ui.onboarding

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboardingProfilePersonalityBinding

class OnboardingProfilePersonalityFragment : Fragment() {

    private var _binding: FragmentOnboardingProfilePersonalityBinding? = null
    private val binding get() = _binding!!

    private val items = listOf(
        "활발함", "차분함", "에너지 폭발", "느긋함", "사교적", "낯가림",
        "의존적", "겁쟁이", "낯가림", "고집 셈", "보호자 중심", "호기심 왕성",
        "독립적", "조용함", "장난꾸러기", "방어적", "스킨십 좋아함",
        "스킨십 싫어함", "직접 입력"
    )
    private val idOfChips: MutableList<Int> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingProfilePersonalityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 강조 텍스트 처리
        val textData = binding.noticeText.text.toString()
        val builder = SpannableStringBuilder(textData).apply {
            val colorGreenSpan = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green_700))
            setSpan(colorGreenSpan, 3, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.noticeText.text = builder

        // Chip 추가
        val chipInflater = LayoutInflater.from(ContextThemeWrapper(requireContext(), R.style.onboarding_chip))
        items.forEachIndexed { index, item ->
            val chip = chipInflater.inflate(R.layout.view_onboarding_chip, binding.chipgroup, false) as Chip
            val id = View.generateViewId()
            chip.id = id
            chip.text = item
            chip.isCheckable = true
            chip.isClickable = true

            chip.setOnClickListener {
                Toast.makeText(requireContext(), "${chip.text} 클릭됨! ID: ${chip.id}", Toast.LENGTH_SHORT).show()
                if (index == 18) { // "직접 입력" 칩
                    binding.customLayout.visibility = if (chip.isChecked) View.VISIBLE else View.INVISIBLE
                }
            }

            idOfChips.add(id)
            binding.chipgroup.addView(chip)
        }

        // 직접 입력 칩 생성
        binding.generateButton.setOnClickListener {
            val inputText = binding.inputEditText.text.toString()
            if (inputText.isNotBlank()) {
                val chip = chipInflater.inflate(R.layout.view_onboarding_chip, binding.chipgroup, false) as Chip
                val id = View.generateViewId()
                chip.id = id
                chip.text = inputText
                chip.isCheckable = true
                chip.isClickable = true
                chip.setOnClickListener {
                    Toast.makeText(requireContext(), "${chip.text} 클릭됨! ID: ${chip.id}", Toast.LENGTH_SHORT).show()
                }
                idOfChips.add(id)
                binding.chipgroup.addView(chip)
                binding.inputEditText.text.clear()
            }
        }

        // 저장 버튼
        binding.saveButton.setOnClickListener {
            // TODO: 선택된 칩 저장 로직 구현
            Toast.makeText(requireContext(), "저장 완료!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

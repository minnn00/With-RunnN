package com.with_runn.ui.onboarding
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboardingProfilePersonalityBinding
import com.with_runn.databinding.FragmentOnboardingProfileWalkingstyleBinding

class OnboardingProfileWalkingStyleFragment : Fragment() {
    private var _binding: FragmentOnboardingProfileWalkingstyleBinding? = null
    private val binding get() = _binding!!

    val items = listOf("빠른 산책","느긋한 산책","속도균형",
        "냄새 탐험","앞서 걷는 타입",
        "보호자 옆 걷기","보호자보다 뒤처지는 타입",
        "강아지 친구 찾기형","사람 친화형",
        "보호자 밀착형","주변 경계형",
        "직접 입력")
    private var idOfChips: MutableList<Int> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingProfileWalkingstyleBinding.inflate(inflater, container, false)
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
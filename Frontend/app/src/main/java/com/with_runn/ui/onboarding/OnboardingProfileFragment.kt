package com.with_runn.ui.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.with_runn.MainActivity
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboardingProfileBinding

class OnboardingProfileFragment : Fragment() {

    private var _binding: FragmentOnboardingProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var name: String

    val viewModel : OnboardingViewmodel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.hasDefaultBeenSet()) {
            binding.entryDefault.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_active)
            binding.defaultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.defaultText.text = viewModel.name.value
            Toast.makeText(requireContext(), "1, ${viewModel.hasDefaultBeenSet()}", Toast.LENGTH_SHORT).show()
        } else {
            binding.entryDefault.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_inactive)
            binding.defaultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_400))
            binding.defaultText.text = "입력"
            Toast.makeText(requireContext(), "2, ${viewModel.hasDefaultBeenSet()}", Toast.LENGTH_SHORT).show()
        }

        if (viewModel.hasCharactersBeenSet()){
            binding.entryPersonality.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_active)
            binding.textPersonality.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            val characterTitle = viewModel.characters.value!!
            if (characterTitle.size > 3) binding.textPersonality.text = characterTitle[0] +", "+ characterTitle[1] +", "+ characterTitle[2] + " 외 ${characterTitle.size.toInt()-3}개"
            else{
                var temp = ""
                for (i in 0 until characterTitle.size) {
                    temp += characterTitle[i]
                    if (i<characterTitle.size-1) temp += ", "
                }
                binding.textPersonality.text = temp
            }
        } else {
            binding.entryPersonality.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_inactive)
            binding.textPersonality.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_400))
            binding.textPersonality.text = "선택"

        }

        if (viewModel.hasStyleBeenSet()){
            binding.entryStyle.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_active)
            binding.textStyle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            val styleTitle = viewModel.style.value!!
            if (styleTitle.size > 3) binding.textStyle.text = styleTitle[0] +", "+ styleTitle[1] +", "+ styleTitle[2] + " 외 ${styleTitle.size-3}개"
            else{
                var temp = ""
                for (i in 0 until styleTitle.size) {
                    temp += styleTitle[i]
                    if (i<styleTitle.size-1) temp += ", "
                }
                binding.textStyle.text = temp
            }
        } else {
            binding.entryStyle.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_inactive)
            binding.textStyle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_400))
            binding.textStyle.text = "선택"

        }

        binding.entryDefault.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingProfileFragment_to_onboardingProfileDefaultFragment)
        }

        binding.entryPersonality.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingProfileFragment_to_onboardingProfilePersonalityFragment)
        }

        binding.entryStyle.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingProfileFragment_to_onboardingProfileWalkingStyleFragment)
        }

        binding.saveButton.setOnClickListener {
            // TODO: 유저 정보 저장 로직 추가
            // 예시: editor.putString("name", name).apply()
            // MainActivity로 이동
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            // 온보딩 액티비티 종료
            requireActivity().finish()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

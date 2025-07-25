package com.with_runn.ui.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        editor = preferences.edit()

        name = preferences.getString("name", "입력").toString()
        binding.defaultText.text = name

        if (name != "입력") {
            binding.entryDefault.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_active)
            binding.defaultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            binding.entryDefault.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_inactive)
            binding.defaultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_400))
        }

        binding.entryDefault.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingProfileFragment_to_onboardingProfileDefaultFragment)
        }

        binding.entryPersonality.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingProfileFragment_to_onboardingProfilePersonalityFragment)
        }

        binding.entryWalkingStyle.setOnClickListener {
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

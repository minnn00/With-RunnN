package com.with_runn.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboardingLoginBinding

class OnboardingLoginFragment : Fragment() {

    private var _binding: FragmentOnboardingLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.naverLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingLoginFragment_to_onboardingProfileFragment)
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingLoginFragment_to_onboardingProfileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

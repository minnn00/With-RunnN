package com.with_runn.ui.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.with_runn.data.model.setProfileResponse
import com.with_runn.data.model.setProfileRequest
import com.with_runn.data.network.ApiClient
import com.with_runn.databinding.FragmentOnboardingProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.Int

class OnboardingProfileFragment : Fragment() {

    private var _binding: FragmentOnboardingProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var name: String
    private var saveable = false

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
            binding.defaultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_700))
            binding.defaultText.text = viewModel.name.value
            Toast.makeText(requireContext(), "1, ${viewModel.hasDefaultBeenSet()}", Toast.LENGTH_SHORT).show()
        } else {
            binding.entryDefault.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_inactive)
            binding.defaultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_400))
            binding.defaultText.text = "입력"
            Toast.makeText(requireContext(), "2, ${viewModel.hasDefaultBeenSet()}", Toast.LENGTH_SHORT).show()
        }

        if (viewModel.hasCharactersBeenSet() && viewModel.characters.value!!.isNotEmpty()){
            binding.entryPersonality.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_active)
            binding.textPersonality.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_700))
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

        if (viewModel.hasStyleBeenSet() && viewModel.style.value!!.isNotEmpty()){
            binding.entryStyle.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_active)
            binding.textStyle.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_700))
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
        if (viewModel.hasDefaultBeenSet() && viewModel.hasCharactersBeenSet() && viewModel.hasStyleBeenSet()){
            saveable = true
            binding.saveButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_main)
        } else {
            saveable = false
            binding.saveButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_entry_inactive)
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
            //반려견 프로필 설정 api 연결 완료
            if (saveable) {
                val characters = viewModel.characters.value!!
                val styles = viewModel.style.value!!
                val request = setProfileRequest(
                    provinceId = 1,
                    cityId = 10,
                    townId = 100,
                    name = viewModel.name.value!!,
                    gender = viewModel.gender.value!!,
                    birth = viewModel.birth.value!!,
                    breed = viewModel.breed.value!!,
                    size = viewModel.size.value!!,
                    characters = characters,
                    style = styles,
                    introduction = viewModel.introduction.value!!
                )
                ApiClient.instance.setProfile(request).enqueue(object : Callback<setProfileResponse> {
                    override fun onResponse(
                        call: Call<setProfileResponse>,
                        response: Response<setProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            val spResponse = response.body()
                            Log.d("success", "성공")
                        } else {
                            Log.e("fail", "실패")
                        }
                    }

                    override fun onFailure(call: Call<setProfileResponse>, t: Throwable) {
                        Log.e("Login", "오류 발생: ${t.message}")
                    }
                })
                //MainActivity로 이동
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)

                // 온보딩 액티비티 종료
                requireActivity().finish()
            }
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

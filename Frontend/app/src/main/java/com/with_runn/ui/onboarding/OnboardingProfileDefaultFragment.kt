package com.with_runn.ui.onboarding

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboardingProfileDefaultBinding
import java.io.File

class OnboardingProfileDefaultFragment : Fragment() {

    private var _binding: FragmentOnboardingProfileDefaultBinding? = null
    private val binding get() = _binding!!

    val viewModel: OnboardingViewmodel by activityViewModels()

    private lateinit var name: String
    private var gender: String = "남"
    private lateinit var birthday: String
    private lateinit var breed: String
    private var size: String = "소형견"
    private var introduction: String = ""

    private var name_saveable: Int = 0
    private var breed_savable: Boolean = false

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingProfileDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!viewModel.hasDefaultBeenSet()){
            setInitialSex("남")
            setInitialSize("소형견")
        }
        else{
            binding.nameEditText.setText(viewModel.name.value)
            binding.breedEditText.setText(viewModel.breed.value)
            binding.birthdayEditText.setText(viewModel.birth.value)
            breed_savable = true
            setInitialSex(viewModel.gender.value!!)
            setInitialSize(viewModel.size.value!!)
        }


        // 저장 버튼 클릭
        binding.saveButton.setOnClickListener {
            name = binding.nameEditText.text.toString()
            birthday = binding.birthdayEditText.text.toString()
            breed = binding.breedEditText.text.toString()
            introduction = binding.discriptionEditText.text.toString()

            if (name_saveable == 0) {
                showNameError("중복 확인을 해주세요")
                Toast.makeText(requireContext(), "이름 중복 확인을 해주세요", Toast.LENGTH_SHORT).show()
            }
            else if (!breed_savable){
                Toast.makeText(requireContext(), "견종은 15자 이내로 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if (name_saveable == 2) {

                viewModel.setDefaultValues(name, gender, birthday, breed, size, introduction)
                // 선택된 이미지가 있으면 업로드
                selectedImageUri?.let { uri ->
                    viewModel.setProfileImg(uri)
                }

                findNavController().popBackStack() // 프로필 프래그먼트로 복귀
            }
        }

        // 성별 버튼 처리
        binding.buttonMale.setOnClickListener { setGender("남") }
        binding.buttonFemale.setOnClickListener { setGender("여") }

        // 크기 버튼 처리
        binding.buttonSmall.setOnClickListener { setSize("소형견") }
        binding.buttonMiddle.setOnClickListener { setSize("중형견") }
        binding.buttonBig.setOnClickListener { setSize("대형견") }

        // 이름 중복 확인
        binding.nameCheckButton.setOnClickListener {
            val currentName = binding.nameEditText.text.toString()
            val savedName = viewModel.name.value

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

        binding.changeImgBtn.setOnClickListener {
            val permission = android.Manifest.permission.READ_MEDIA_IMAGES
            requestPermissionLauncher.launch(permission)
        }
    }

    // 1️⃣ 권한 요청
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("권한 필요")
                    .setMessage("이미지를 업로드하려면 권한이 필요합니다. 설정에서 권한을 허용해주세요.")
                    .setPositiveButton("설정") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireContext().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }

    // 2️⃣ 갤러리에서 이미지 선택
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                Glide.with(this).load(it).circleCrop().into(binding.profileImage)
            }
        }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
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

    private fun setGender(selected: String) {
        gender = selected
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
        setGender(sexValue)
    }

    private fun setInitialSize(sizeValue: String) {
        setSize(sizeValue)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

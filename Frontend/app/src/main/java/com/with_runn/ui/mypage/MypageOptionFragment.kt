package com.with_runn.ui.mypage

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.with_runn.ActivityViewModel
import com.with_runn.databinding.DialogAskBinding
import com.with_runn.databinding.DialogInfoBinding
import com.with_runn.databinding.FragmentMypageOptionBinding
import com.with_runn.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MypageOptionFragment : Fragment(){
    private var _binding: FragmentMypageOptionBinding? = null
    private val binding get() = _binding!!

    private val activityVM : ActivityViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageOptionBinding.inflate(inflater, container, false)
        activityVM.setUpperToolbarVisibility(false)
        activityVM.setBottomNavVisibility(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogout.setOnClickListener {
            val onPositiveClick = {
                viewLifecycleOwner.lifecycleScope.launch {
                    activityVM.logout()
                    startActivity(
                        Intent(requireContext(), OnboardingActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                    requireActivity().finish()
                }
            }
            showAskDialog(requireContext(), "로그아웃 하시겠습니까?", "로그아웃", "취소",onPositiveClick)
        }
        binding.btnDrop.setOnClickListener {
            val onPositiveClick = {
                viewLifecycleOwner.lifecycleScope.launch {
                    val ok = activityVM.deleteAccount()
                    if (ok) {
                        startActivity(
                            Intent(requireContext(), OnboardingActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                        requireActivity().finish()
                } else {
                    showInfoDialog(requireContext(), "탈퇴에 실패했습니다.\n잠시 후 다시 시도해 주세요.", "닫기")
                }
            }}
            showAskDialog(requireContext(), "회원탈퇴 하시겠습니까?", "탈퇴하기", "취소",onPositiveClick)
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    fun showAskDialog(
        context: Context,
        message: String,
        positiveText: String = "확인",
        negativeText: String = "취소",
        onPositiveClick: (() -> kotlinx.coroutines.Job?) = { null },
        onNegativeClick: () -> Unit = {}
    ) {
        val binding = DialogAskBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.tvAsk.text = message
        binding.btnYes.text = positiveText
        binding.btnCancel.text = negativeText

        binding.btnYes.setOnClickListener {
            val job = try { onPositiveClick() } catch (_: Throwable) { null }

            if (job == null || job.isCompleted || !job.isActive) {
                dialog.dismiss()
            } else {
                // 진행 중엔 버튼 비활성/취소불가
                binding.btnYes.isEnabled = false
                binding.btnCancel.isEnabled = false
                dialog.setCancelable(false)

                job.invokeOnCompletion {
                    // 메인 스레드에서 닫기
                    binding.root.post { dialog.dismiss() }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            onNegativeClick()
            dialog.dismiss()
        }

        dialog.show()
    }



    fun showInfoDialog(
        context: Context,
        message: String,
        positiveText: String = "확인",
        onPositiveClick: () -> Unit = {}
    ) {
        val binding = DialogInfoBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvMessage = binding.tvAsk
        val btnPositive = binding.btnYes

        tvMessage.text = message
        btnPositive.text = positiveText

        btnPositive.setOnClickListener {
            onPositiveClick()
            dialog.dismiss()
        }

        dialog.show()
    }
}
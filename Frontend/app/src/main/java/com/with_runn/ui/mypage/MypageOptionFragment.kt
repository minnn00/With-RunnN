package com.with_runn.ui.mypage

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.with_runn.ActivityViewModel
import com.with_runn.databinding.DialogAskBinding
import com.with_runn.databinding.DialogInfoBinding
import com.with_runn.databinding.FragmentMypageOptionBinding

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
            val onPositiveClick = {showInfoDialog(requireContext(), "로그아웃 하였습니다.", "닫기")}
            showAskDialog(requireContext(), "로그아웃 하시겠습니까?", "로그아웃", "취소",onPositiveClick)
        }
        binding.btnDrop.setOnClickListener {
            val onPositiveClick = {showInfoDialog(requireContext(), "탈퇴하였습니다.", "닫기")}
            showAskDialog(requireContext(), "회원탈퇴 하시겠습니까?", "탈퇴하기", "취소",onPositiveClick)
        }
    }


    fun showAskDialog(
        context: Context,
        message: String,
        positiveText: String = "확인",
        negativeText: String = "취소",
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {}
    ) {
        val binding = DialogAskBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvMessage = binding.tvAsk
        val btnPositive = binding.btnYes
        val btnNegative = binding.btnCancel

        tvMessage.text = message
        btnPositive.text = positiveText
        btnNegative.text = negativeText

        btnPositive.setOnClickListener {
            onPositiveClick()
            dialog.dismiss()
        }

        btnNegative.setOnClickListener {
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
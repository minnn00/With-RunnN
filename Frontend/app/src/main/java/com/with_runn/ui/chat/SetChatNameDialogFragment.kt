package com.with_runn.ui.chat

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.with_runn.R
import com.with_runn.databinding.DialogSetChatNameBinding

class SetChatNameDialogFragment : DialogFragment() {

    private var _binding: DialogSetChatNameBinding? = null
    private val binding get() = _binding!!
    
    private var onChatNameChanged: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSetChatNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        // 텍스트 변경 감지
        binding.etChatName.addTextChangedListener { editable ->
            val text = editable.toString().trim()
            val isValid = text.isNotBlank()
            
            // 버튼 활성화 상태 변경
            binding.btnConfirm.isEnabled = isValid
            
            // 버튼 배경색 변경
            binding.btnConfirm.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (isValid) R.color.green_700 else R.color.gray_400
            )
        }
    }

    private fun setupClickListeners() {
        binding.btnConfirm.setOnClickListener {
            val chatName = binding.etChatName.text.toString().trim()
            if (chatName.isNotBlank()) {
                onChatNameChanged?.invoke(chatName)
                dismiss()
            }
        }
    }

    fun setOnChatNameChangedListener(listener: (String) -> Unit) {
        onChatNameChanged = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): SetChatNameDialogFragment {
            return SetChatNameDialogFragment()
        }
    }
} 
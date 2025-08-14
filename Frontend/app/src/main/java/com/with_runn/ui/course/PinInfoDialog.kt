package com.with_runn.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.with_runn.databinding.DialogPinInfoBinding

class PinInfoDialogFragment : DialogFragment() {

    private var pinName: String? = null
    private var pinDetail: String? = null

    private var _binding: DialogPinInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val a = requireArguments()
        pinName = a.getString(ARG_NAME)
        pinDetail = a.getString(ARG_DETAIL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         _binding = DialogPinInfoBinding.inflate(inflater, container, false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            titleText.text = pinName ?: "산책 스팟"
            contentText.text = pinDetail ?: "꼭 들러 봐요!"

            closeBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply{
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_NAME = "pin_name"
        private const val ARG_DETAIL = "pin_detail"

        fun newInstance(args: Bundle) = PinInfoDialogFragment().apply {
            arguments = args
        }

        fun newInstance(name: String?, detail: String?) = PinInfoDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NAME, name)
                putString(ARG_DETAIL, detail)
            }
        }
    }
}

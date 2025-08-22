package com.with_runn.ui.course_edit

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.shape.ShapeAppearanceModel
import com.with_runn.R
import com.with_runn.databinding.DialogPinEditBinding
import com.with_runn.dp

class PinEditDialogFragment : DialogFragment() {
    companion object{
        private const val ARG_PIN = "arg_pin"
        private const val ARG_MODE = "arg_mode"

        enum class Mode{ADD, EDIT}

        fun newInstance(pin: PinItem, mode: Mode): PinEditDialogFragment{
            return PinEditDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PIN, pin)
                    putSerializable(ARG_MODE, mode)
                }
            }
        }
    }

    private var _binding: DialogPinEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var pin : PinItem
    private lateinit var mode : Mode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogPinEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: SDK 버전에 따른 분기
        pin = BundleCompat.getParcelable(requireArguments(), ARG_PIN, PinItem::class.java)
            ?: error("Pin argument required")

        mode = BundleCompat.getSerializable(requireArguments(), ARG_MODE, Mode::class.java)
            ?: error("Mode argument required")

        binding.apply {
            // 이름
            pinName.setText(pin.name)
            // 설명
            pinInfo.setText(pin.content)
            // TODO: 색상: pin이 있으면 해당 색상 chip 체크, 아니면 0번 chip 체크

            binding.apply {
                val enabled = pin.name != ""
                btnConfirm.isEnabled = enabled
                btnConfirm.setBackgroundResource(
                    if (enabled) R.drawable.bg_btn_filled else R.drawable.bg_btn_filled_inactive
                )
            }
        }

        setupChipGroup()
        setListeners()
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

    private fun setListeners(){
        binding.apply {
            pinName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val enabled = !s.isNullOrBlank()
                    btnConfirm.isEnabled = enabled
                    btnConfirm.setBackgroundResource(
                        if (enabled) R.drawable.bg_btn_filled else R.drawable.bg_btn_filled_inactive
                    )
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            btnCancel.setOnClickListener {
                Log.d("BTN", "Clicked!")
                AlertDialog.Builder(requireContext())
                    .setMessage("취소하시겠습니까?")
                    .setPositiveButton("예") { _, _ -> dismiss() }
                    .setNegativeButton("아니오", null)
                    .show()
            }

            btnConfirm.setOnClickListener {
                pin.name = binding.pinName.text.toString()
                pin.content = binding.pinInfo.text.toString()

                val result = Bundle().apply{
                    putParcelable("pin_item", pin)
                    putSerializable("mode", mode)
                }
                parentFragmentManager.setFragmentResult("pin_edit_result", result)
                dismiss()
            }
        }
    }

    private fun setupChipGroup() {
        val pinColors = listOf(
            R.color.pin_red,
            R.color.pin_orange,
            R.color.pin_yellow,
            R.color.pin_green,
            R.color.pin_cyan,
            R.color.pin_blue,
            R.color.pin_purple
        )

        binding.chipGroup.removeAllViews()

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as? Chip ?: continue
                chip.chipStrokeWidth = if (chip.isChecked) 1.dp.toFloat() else 0f
            }
        }

        pinColors.forEachIndexed { idx, colorRes ->
            Chip(requireContext()).apply{
                isCheckable = true
                isClickable = true
                text=""
                chipStrokeWidth = 0f
                chipStrokeColor = ContextCompat.getColorStateList(context, R.color.gray_950)
                shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(18.dp.toFloat())
                chipBackgroundColor = ContextCompat.getColorStateList(context, colorRes)

                tag = colorRes

                if (idx == 0) {
                    isChecked = true
                    chipStrokeWidth = 1.dp.toFloat()
                }

                binding.chipGroup.addView(this)
            }
        }
    }
}
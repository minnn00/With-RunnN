package com.with_runn.ui.course_edit

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.with_runn.R
import com.with_runn.databinding.DialogCourseEditBinding
import com.with_runn.formatMinutesToHM
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseData(
    var title : String,
    var info : String?,
    var keyword: String?,
    var time: Int
) : Parcelable

class CourseEditDialogFragment : DialogFragment(){
    companion object{
        private const val ARG_COURSE = "arg_course"

        fun newInstance(data: CourseData): CourseEditDialogFragment{
            return CourseEditDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_COURSE, data)
                }
            }
        }
    }

    private var _binding: DialogCourseEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var course : CourseData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogCourseEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        course = requireArguments().getParcelable<CourseData>(ARG_COURSE, CourseData::class.java) ?: error("Course argument required")

        binding.apply{
            courseName.setText(course.title)
            courseInfo.setText(course.info)
            courseTime.text = formatMinutesToHM(course.time)

            binding.apply {
                val enabled = course.title != ""
                btnConfirm.isEnabled = enabled
                btnConfirm.setBackgroundResource(
                    if (enabled) R.drawable.bg_btn_filled else R.drawable.bg_btn_filled_inactive
                )
            }
        }

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
        binding.apply{
            courseName.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val enabled = !s.isNullOrBlank()
                        btnConfirm.isEnabled = enabled
                        btnConfirm.setBackgroundResource(
                            if (enabled) R.drawable.bg_btn_filled else R.drawable.bg_btn_filled_inactive
                        )
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })

            btnCancel.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setMessage("취소하시겠습니까?")
                    .setPositiveButton("예") { _, _ -> dismiss() }
                    .setNegativeButton("아니오", null)
                    .show()
            }

            btnConfirm.setOnClickListener {
                course.title = binding.courseName.text.toString()
                course.info = binding.courseInfo.text.toString()

                val result = Bundle().apply{
                    putParcelable("course_data", course)
                }

                parentFragmentManager.setFragmentResult("course_edit_result", result)
                dismiss()
            }
        }
    }

}
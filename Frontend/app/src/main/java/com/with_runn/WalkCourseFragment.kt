package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.with_runn.databinding.FragmentWalkCourseBinding


class WalkCourseFragment : Fragment() {

    private var _binding: FragmentWalkCourseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textLocalMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_localMoreFragment)
        }

        binding.textHotMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_hotMoreFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

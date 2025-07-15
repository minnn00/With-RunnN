package com.with_runn.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboarding1Binding

class OnboardingFragment1 : Fragment() {

    private lateinit var binding: FragmentOnboarding1Binding

    companion object {
        fun newInstance(title: String, description: String, imgID: Int): OnboardingFragment1 {
            val fragment = OnboardingFragment1()
            fragment.arguments = Bundle().apply {
                putString("title", title)
                putString("description", description)
                putInt("imgID", imgID)
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = arguments?.getString("title")
        val description = arguments?.getString("description")
        val imgID = arguments?.getInt("imgID") ?: R.drawable.ic_app_logo
        binding.textView.text = title
        binding.textView2.text = description
        binding.imageView.setImageResource(imgID)
    }

    // 프래그먼트 잘림 오류 방지 위하여 적용
    override fun onResume(){
        super.onResume()
        binding.root.requestLayout()

    }
}
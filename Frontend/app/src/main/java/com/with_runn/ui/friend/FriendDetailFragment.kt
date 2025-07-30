package com.with_runn.ui.friend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.with_runn.R

class FriendDetailFragment : Fragment() {
    
    companion object {
        private const val ARG_DOG_NAME = "dog_name"
        private const val ARG_DOG_AGE = "dog_age"
        private const val ARG_DOG_BREED = "dog_breed"
        private const val ARG_DOG_CATEGORY = "dog_category"
        private const val ARG_DOG_INTRO = "dog_intro"
        
        fun newInstance(
            dogName: String = "조니",
            dogAge: String = "3년 6개월",
            dogBreed: String = "래브라도 리트리버",
            dogCategory: String = "대형견",
            dogIntro: String = "안녕하세요! 달리기 좋아하는 3살 조니예요 :)"
        ): FriendDetailFragment {
            val fragment = FriendDetailFragment()
            val args = Bundle()
            args.putString(ARG_DOG_NAME, dogName)
            args.putString(ARG_DOG_AGE, dogAge)
            args.putString(ARG_DOG_BREED, dogBreed)
            args.putString(ARG_DOG_CATEGORY, dogCategory)
            args.putString(ARG_DOG_INTRO, dogIntro)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_detail, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 전달받은 데이터 설정
        setupDogInfo()
        
        // 버튼 클릭 리스너 설정
        setupButtonListeners()
    }
    
    private fun setupDogInfo() {
        // 전달받은 강아지 정보 설정
        val dogName = arguments?.getString(ARG_DOG_NAME) ?: "조니"
        val dogAge = arguments?.getString(ARG_DOG_AGE) ?: "3년 6개월"
        val dogBreed = arguments?.getString(ARG_DOG_BREED) ?: "래브라도 리트리버"
        val dogCategory = arguments?.getString(ARG_DOG_CATEGORY) ?: "대형견"
        val dogIntro = arguments?.getString(ARG_DOG_INTRO) ?: "안녕하세요! 달리기 좋아하는 3살 조니예요 :)"
        
        view?.findViewById<TextView>(R.id.dog_name)?.text = dogName
        view?.findViewById<TextView>(R.id.dog_age)?.text = dogAge
        view?.findViewById<TextView>(R.id.dog_breed)?.text = dogBreed
        view?.findViewById<TextView>(R.id.dog_category)?.text = dogCategory
        view?.findViewById<TextView>(R.id.dog_intro)?.text = dogIntro
        
        // jonny.png는 그대로 사용
        view?.findViewById<ImageView>(R.id.dog_image)?.setImageResource(R.drawable.jonny)
        view?.findViewById<ImageView>(R.id.profile_image)?.setImageResource(R.drawable.jonny)
    }
    
    private fun setupButtonListeners() {
        // 팔로우 버튼
        view?.findViewById<TextView>(R.id.follow_button)?.setOnClickListener {
            // 팔로우 기능 구현
        }
        
        // 메시지 버튼
        view?.findViewById<TextView>(R.id.message_button)?.setOnClickListener {
            // 메시지 기능 구현
        }
        
        // 메뉴 버튼
        view?.findViewById<ImageView>(R.id.menu_button)?.setOnClickListener {
            Log.d("FriendDetailFragment", "메뉴 버튼 클릭됨")
            showMenuPopup(it)
        }
    }
    
    private fun showMenuPopup(anchorView: View) {
        Log.d("FriendDetailFragment", "showMenuPopup 호출됨")
        
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_menu, null)
        
        // 팝업 뷰의 크기를 측정
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        
        Log.d("FriendDetailFragment", "팝업 크기: ${popupView.measuredWidth} x ${popupView.measuredHeight}")
        
        val popupWindow = PopupWindow(
            popupView,
            popupView.measuredWidth,
            popupView.measuredHeight
        )
        
        // 팝업 윈도우 설정
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 10f
        
        // 팝업 표시 (메뉴 버튼 우측 상단)
        popupWindow.showAsDropDown(anchorView, -popupWindow.width + anchorView.width, 0)
        
        Log.d("FriendDetailFragment", "팝업 표시됨")
        
        // 팝업 메뉴 아이템 클릭 리스너
        popupView.findViewById<View>(R.id.block_button)?.setOnClickListener {
            Log.d("FriendDetailFragment", "차단하기 클릭됨")
            // 차단하기 기능 구현
            popupWindow.dismiss()
        }
        
        popupView.findViewById<View>(R.id.report_button)?.setOnClickListener {
            Log.d("FriendDetailFragment", "신고하기 클릭됨")
            // 신고하기 기능 구현
            popupWindow.dismiss()
        }
    }
} 
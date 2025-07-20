//package com.with_runn.ui.mypage
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.with_runn.databinding.FragmentMypageFollowersBinding
//
//class MypageFollowersFragment : Fragment(){
//    private lateinit var binding: FragmentMypageFollowersBinding
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: MypageFollowersRecyclerViewAdaper
//
//    private lateinit var profiles: List<String>
//
//    companion object {
//        private const val ARG_TEXT = "text"
//
//        fun newInstance(text: String): MypageFollowersFragment {
//            val fragment = MypageFollowersFragment()
//            val args = Bundle()
//            args.putString(ARG_TEXT, text)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentMypageFollowersBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val text = arguments?.getString(ARG_TEXT) ?: "모든 팔로워"
//        binding.titleText.text = text
//
//        profiles = when (text) {
//            "모든 팔로워" -> listOf("aaa", "bbb", "ccc")
//            "모든 팔로잉" -> listOf("ddd", "eee", "fff")
//            else -> emptyList()
//        }
//        recyclerView = binding.followersRecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        adapter = MypageFollowersRecyclerViewAdaper(profiles)
//        recyclerView.adapter = adapter
//    }
//}

package com.with_runn.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentMypageFollowersBinding

class MypageFollowersFragment : Fragment() {

    private var _binding: FragmentMypageFollowersBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_TAB_TITLE = "tab_title"

        fun newInstance(tabTitle: String): MypageFollowersFragment {
            val fragment = MypageFollowersFragment()
            val args = Bundle()
            args.putString(ARG_TAB_TITLE, tabTitle)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabTitle = arguments?.getString(ARG_TAB_TITLE) ?: "모든 사용자"
        binding.titleText.text = tabTitle

        val profiles = when (tabTitle) {
            "모든 팔로워" -> listOf("aaa", "bbb", "ccc")
            "모든 팔로잉" -> listOf("ddd", "eee", "fff")
            "차단 유저" -> listOf("zzz", "yyy")
            else -> emptyList()
        }

        binding.followersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.followersRecyclerView.adapter = MypageFollowersRecyclerViewAdaper(profiles)
    }

    override fun onResume(){
        super.onResume()
        binding.root.requestLayout()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

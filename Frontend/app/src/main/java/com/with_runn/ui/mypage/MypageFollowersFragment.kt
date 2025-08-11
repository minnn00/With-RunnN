package com.with_runn.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.data.model.Follower
import com.with_runn.data.viewmodel.MypageFollowerViewmodel
import com.with_runn.databinding.FragmentMypageFollowersBinding

class MypageFollowersFragment : Fragment() {

    private var _binding: FragmentMypageFollowersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MypageFollowerViewmodel by viewModels()

    private lateinit var adapter: MypageFollowersRecyclerViewAdapter

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

        binding.followersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MypageFollowersRecyclerViewAdapter(
            listOf(),
            viewModel,
            viewLifecycleOwner)
        binding.followersRecyclerView.adapter = adapter

        if (tabTitle == "모든 팔로워"){
            viewModel.loadFollowers()
            viewModel.followers.observe(viewLifecycleOwner) { list ->
                adapter.updateFollowerData(list)
                Log.d("MypageFollowers", "followers size: ${list.size}")
                val numtext = list.size.toString() + "명"
                binding.numsText.text = numtext
            }
        }
        else{
            viewModel.loadFollowings()
            viewModel.followings.observe(viewLifecycleOwner) { list ->
                adapter.updateFollowingData(list)
                Log.d("MypageFollowers", "followers size: ${list.size}")
                val numtext = list.size.toString() + "명"
                binding.numsText.text = numtext
            }
        }
        binding.root.requestLayout()
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

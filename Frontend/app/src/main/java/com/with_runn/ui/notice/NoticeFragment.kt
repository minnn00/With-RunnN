package com.with_runn.ui.notice

import NoticeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.ActivityViewModel
import com.with_runn.data.model.FollowResponse
import com.with_runn.data.model.Notice
import com.with_runn.data.network.ApiClient
import com.with_runn.data.network.ApiService
import com.with_runn.data.repository.NoticeRepository
import com.with_runn.data.viewmodel.MypageFollowerViewmodel
import com.with_runn.data.viewmodel.NoticeViewModel
import com.with_runn.data.viewmodel.NoticeViewModelFactory
import com.with_runn.databinding.FragmentNoticeBinding
import com.with_runn.ui.mypage.MypageUserProfileDialogFragment
import kotlinx.coroutines.launch

class NoticeFragment : Fragment() {

    private var _binding: FragmentNoticeBinding? = null
    private val binding get() = _binding!!

    private lateinit var noticeVM: NoticeViewModel
    private val followVM: MypageFollowerViewmodel by viewModels()

    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var api: ApiService

    private val activityVM: ActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // DI 미사용 가정: 직접 구성
        api = ApiClient.instance
        val repository = NoticeRepository(api)
        noticeVM = ViewModelProvider(this, NoticeViewModelFactory(repository))[NoticeViewModel::class.java]

        activityVM.setBottomNavVisibility(false)
        activityVM.setUpperToolbarVisibility(false)

        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 단일 레이아웃 + 콜백 기반 어댑터
        noticeAdapter = NoticeAdapter(
            callbacks = object : NoticeAdapter.Callbacks {
                override fun onItemClick(notice: Notice) { /* 필요 시 */ }

                override fun onProfileClick(actorId: Int) {
                    MypageUserProfileDialogFragment
                        .newInstance(actorId)
                        .show(childFragmentManager, "FriendProfileDialog")
                }

                override suspend fun onFollowClick(notice: Notice): FollowResponse {
                    // 네트워크 호출 -> FollowResponse 그대로 어댑터에 반환
                    return followVM.followUserAwait(notice.actorId)
                }
            },
            scope = viewLifecycleOwner.lifecycleScope
        )

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.noticeItemRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noticeAdapter
        }

        // 최초 로드
        loadNotices()
    }

    private fun loadNotices() {
        lifecycleScope.launch {
            try {
                val notices: List<Notice> = api.getNotices()
                // ListAdapter 사용: submitList
                noticeAdapter.submitList(notices)
            } catch (e: Exception) {
                e.printStackTrace()
                // 필요 시 에러 처리(UI 표시 등) 추가
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

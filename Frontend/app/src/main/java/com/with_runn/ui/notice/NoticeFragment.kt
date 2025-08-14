package com.with_runn.ui.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.ActivityViewModel
import com.with_runn.R
import com.with_runn.data.model.Notice
import com.with_runn.data.network.ApiClient
import com.with_runn.data.network.ApiService
import com.with_runn.data.repository.NoticeRepository
import com.with_runn.data.viewmodel.MypageFollowerViewmodel
import com.with_runn.data.viewmodel.NoticeViewModel
import com.with_runn.data.viewmodel.NoticeViewModelFactory
import kotlinx.coroutines.launch

class NoticeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: NoticeViewModel
    private val userViewModel: MypageFollowerViewmodel by viewModels()
    private lateinit var noticeAdapter: NoticeAdapter
    private val noticeList = mutableListOf<Notice>()

    private val activityVM: ActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityVM.setBottomNavVisibility(false)
        activityVM.setUpperToolbarVisibility(false)

        val view = inflater.inflate(R.layout.fragment_notice, container, false)
        val api = ApiClient.instance
        val repository = NoticeRepository(api)
        viewModel = ViewModelProvider(this, NoticeViewModelFactory(repository)).get(NoticeViewModel::class.java)

        recyclerView = view.findViewById(R.id.notice_item_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        noticeAdapter = NoticeAdapter(noticeList, childFragmentManager,userViewModel)
        recyclerView.adapter = noticeAdapter
        loadNotices(api)
        return view
    }

    private fun loadNotices(api: ApiService) {
        lifecycleScope.launch {
            try {
                val notices = api.getNotices()
                noticeList.clear()
                noticeList.addAll(notices)
                noticeAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
package com.with_runn.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.with_runn.ActivityViewModel
import com.with_runn.databinding.DialogShareBottomSheetBinding
import kotlinx.coroutines.launch

class ShareBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogShareBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val vm: ShareSheetViewModel by viewModels()
    private val activityVM: ActivityViewModel by activityViewModels()

    private val courseId by lazy {
        requireArguments().getInt(ARG_COURSE_ID)
    }

    private lateinit var shareAdapter: ChatRoomShareAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogShareBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        // RecyclerView 구성 (3열)
        shareAdapter = ChatRoomShareAdapter { room ->
            // 선택 시 확인 팝업
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("공유 확인")
                .setMessage("공유 하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { _, _ ->
                    // 공유 API 호출 (성공/실패에 따라 dismiss)
                    lifecycleScope.launch {
                        binding.progress.visibility = View.VISIBLE
                        val success = activityVM.memberId.value?.let { uid ->
                            vm.shareCourseToRoom(uid, courseId, room.chatId)
                        } ?: run {
                            Log.w("ShareSheet", "memberId is null → auto-fail")
                            false
                        }
                        binding.progress.visibility = View.GONE
                        if (success) {
                            parentFragmentManager.setFragmentResult(
                                REQ_SHARE_RESULT,
                                Bundle().apply {
                                    putBoolean(KEY_SHARE_SUCCESS, true)
                                    putInt(KEY_ROOM_ID, room.chatId)
                                    putString(KEY_ROOM_NAME, room.name)
                                }
                            )
                        } else {
                            // 실패 처리 (필요 시 안내)
                            Toast.makeText(requireContext(), "코스를 공유하지 못했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        if (parentFragmentManager.isStateSaved) dismissAllowingStateLoss() else dismiss()
                    }
                }
                .show()
        }

        binding.recycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = shareAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.rooms.collect { list ->
                Log.d("ShareSheet", "adapter@collect=${System.identityHashCode(shareAdapter)}")
                shareAdapter.submitList(list){
                    Log.d("ShareSheet", "commit size=${shareAdapter.itemCount}, current=${shareAdapter.currentList.size}")
                }
                Log.d("ShareSheet", "collect size=${list.size} -> adapterCount=${shareAdapter.itemCount}")
                binding.empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.progress.visibility = View.VISIBLE
            vm.loadRooms()
            binding.progress.visibility = View.GONE
        }

        // 닫기 버튼(optional)
        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_COURSE_ID = "arg_course_id"

        const val REQ_SHARE_RESULT = "req_share_result"
        const val KEY_SHARE_SUCCESS = "key_share_success"
        const val KEY_ROOM_ID = "key_room_id"
        const val KEY_ROOM_NAME = "key_room_name"

        fun newInstance(courseId: Int) = ShareBottomSheetDialogFragment().apply {
            arguments = Bundle().apply { putInt(ARG_COURSE_ID, courseId) }
        }
    }
}

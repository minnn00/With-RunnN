package com.with_runn.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.with_runn.databinding.DialogShareBottomSheetBinding
import kotlinx.coroutines.launch

class ShareBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogShareBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val vm: ShareSheetViewModel by viewModels()

    private val courseId by lazy {
        requireArguments().getInt(ARG_COURSE_ID)
    }

    private lateinit var adapter: ChatRoomShareAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogShareBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        // RecyclerView 구성 (3열)
        adapter = ChatRoomShareAdapter { room ->
            // 선택 시 확인 팝업
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("공유 확인")
                .setMessage("공유 하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { _, _ ->
                    // 공유 API 호출 (성공/실패에 따라 dismiss)
                    lifecycleScope.launch {
                        binding.progress.visibility = View.VISIBLE
                        val success = vm.shareCourseToRoom(courseId, room.chatId)
                        binding.progress.visibility = View.GONE
                        dismiss()
                        vm.emitNavigateToRoomIfSuccess(success, room)
                    }
                }
                .show()
        }

        binding.recycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = this@ShareBottomSheetDialogFragment.adapter
            setHasFixedSize(true)
        }

        // 데이터 fetch (API는 나중에 연결)
        lifecycleScope.launch {
            binding.progress.visibility = View.VISIBLE
            vm.loadRooms()                   // 추후 실제 API 연동
            binding.progress.visibility = View.GONE
            adapter.submitList(vm.rooms.value)
            binding.empty.visibility = if (vm.rooms.value.isEmpty()) View.VISIBLE else View.GONE
        }

        // 닫기 버튼(optional)
        binding.btnClose.setOnClickListener { dismiss() }

        // 공유 성공 시 외부로 이벤트 전달 (FragmentResult 사용)
        vm.navigateEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { room ->
                parentFragmentManager.setFragmentResult(
                    REQ_SHARE_RESULT,
                    Bundle().apply {
                        putBoolean(KEY_SHARE_SUCCESS, true)
                        putInt(KEY_ROOM_ID, room.chatId)
                        putString(KEY_ROOM_NAME, room.name)
                    }
                )
            }
        }
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

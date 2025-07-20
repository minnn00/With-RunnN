package com.with_runn.ui.course

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.with_runn.PinItem
import com.with_runn.PinListAdapter
import com.with_runn.databinding.FragmentCourseManageBinding
import com.with_runn.course_edit.PinItem
import com.with_runn.course_edit.PinListAdapter

class CourseManageFragment : Fragment() {

    private var _binding : FragmentCourseManageBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemTouchHelper : ItemTouchHelper


    private lateinit var pinListAdapter : PinListAdapter

    val sampleData = listOf<PinItem>(
        PinItem("빵집", "빵집입니다.", "288.233", "#FFFFFF"),
        PinItem("카페", "카페입니다.", "241.209", "#FFFFFF"),
        PinItem("술집", "술집입니다.", "249.577", "#FFFFFF"),
        PinItem("개집", "개집입니다.", "086.418", "#FFFFFF"),
        PinItem("새집", "새집입니다.", "696.324", "#FFFFFF")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCourseManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

        pinListAdapter = PinListAdapter(
            onItemClick = { item -> onClickItem(item) },
            onItemDrag = { vh -> itemTouchHelper.startDrag(vh) }
        )

        binding.pinListRcv.apply{
            adapter = pinListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        itemTouchHelper.attachToRecyclerView(binding.pinListRcv)
        pinListAdapter.submitList(sampleData)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBottomSheet(){

        val bottomSheet = binding.bottomSheetBehaviour
        val behavior = BottomSheetBehavior.from(bottomSheet)

        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.isHideable = false
    }

    private fun onClickItem(item: PinItem){
        Log.d("TEST", "${item.name} clicked!")
        // TODO: 선택된 Item으로 focus 이동, 이미 Focus라면 수정 화면 열기

    }

    private fun moveItem(from: Int, to: Int){
        if(from == to) return;

        val mutableList = sampleData.toMutableList() // TODO: ViewModel의 실제 데이터에 연결
        val item = mutableList.removeAt(from)
        mutableList.add(to, item)
        pinListAdapter.submitList(mutableList)
    }

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            moveItem(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        override fun isLongPressDragEnabled() = false
    }
}
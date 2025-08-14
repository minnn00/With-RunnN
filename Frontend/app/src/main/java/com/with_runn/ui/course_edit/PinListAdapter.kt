package com.with_runn.ui.course_edit

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemPinListBinding
import kotlinx.parcelize.Parcelize

@Parcelize
data class PinItem(
    var index: Int,
    var name: String = "",
    var content: String = "",
    val lat: Double,
    val lng: Double
) : Parcelable

class PinListAdapter(
    private val onItemClick: (PinItem) -> Unit,
    private val onItemDrag: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<PinListAdapter.ViewHolder>() {

    private val items: MutableList<PinItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPinListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            pinIndex.text = (item.index).toString()
            pinName.text = item.name

            pinName.setOnClickListener {
                onItemClick(item)
            }

            pinHandle.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    onItemDrag(holder)
                }
                false
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemPinListBinding) : RecyclerView.ViewHolder(binding.root)

    /** 리스트 전체를 교체 */
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<PinItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    /** 아이템 위치 교체 후 애니메이션 */
    fun swapItem(from: Int, to: Int) {
        if (from == to) return
        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
    }

    fun removeItem(position: Int): PinItem {
        val removed = items.removeAt(position)
        notifyItemRemoved(position)
        return removed
    }

    fun getCurrentList(): List<PinItem> = items.toList()
}
package com.with_runn

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemPinListBinding

data class PinItem(
    val name: String,
    val description: String = "",
    val coordinate: String,
    val pinColor: String,
)

class PinListAdapter (
    private val onItemClick: (PinItem) -> Unit,
    private val onItemDrag: (RecyclerView.ViewHolder) -> Unit
) : ListAdapter<PinItem, PinListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPinListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.apply{
            pinIndex.text = (position + 1).toString()
            pinName.text = item.name

            pinName.setOnClickListener {
                onItemClick(item)
            }

            pinHandle.setOnTouchListener { _, event ->
                if(event.actionMasked == MotionEvent.ACTION_DOWN){
                    onItemDrag(holder)
                }
                false
            }
        }

    }

    inner class ViewHolder(val binding: ItemPinListBinding) : RecyclerView.ViewHolder(binding.root)

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<PinItem>(){
            override fun areItemsTheSame(oldItem: PinItem, newItem: PinItem): Boolean = oldItem.coordinate == newItem.coordinate
            override fun areContentsTheSame(oldItem: PinItem, newItem: PinItem): Boolean = oldItem == newItem
        }
    }
}
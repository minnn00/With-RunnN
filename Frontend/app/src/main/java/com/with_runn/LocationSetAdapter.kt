package com.with_runn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.data.region.RegionResponse
import com.with_runn.databinding.ItemRegionBinding


data class RegionItem(
    val id: Int?,      // "[지역] 전체"는 null
    val name: String
)

class LocationSetAdapter (
    private val onItemClick: (RegionItem) -> Unit,
) : RecyclerView.Adapter<LocationSetAdapter.ViewHolder>() {

    private val items: MutableList<RegionItem> = mutableListOf()
    private var selectedId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            regionText.text = item.name

            val ctx = root.context
            val bg = if (item.id == selectedId)
                ContextCompat.getColor(ctx, R.color.gray_400)
            else
                ContextCompat.getColor(ctx, R.color.gray_050)
            root.setBackgroundColor(bg)

            val tc = if (item.id == selectedId)
                ContextCompat.getColor(ctx, R.color.green_700)
            else
                ContextCompat.getColor(ctx, R.color.gray_950)
            regionText.setTextColor(tc)

            root.setOnClickListener {
                if (selectedId == item.id) return@setOnClickListener

                // [FIX] 이전 포지션을 'null' 포함해서 항상 계산
                val prevId = selectedId
                val prevPos = items.indexOfFirst { it.id == prevId }

                selectedId = item.id

                // [FIX] 이전이 null이었어도 올바르게 갱신
                if (prevPos >= 0) notifyItemChanged(prevPos)
                notifyItemChanged(holder.bindingAdapterPosition)

                onItemClick(item)
            }
        }
    }


    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemRegionBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newList: List<RegionItem>) {
        items.clear()
        selectedId = null
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun reset(){
        selectedId = null
        notifyDataSetChanged()
    }

    fun selectById(id: Int?) {
        val old = selectedId
        selectedId = id
        if (old != null) {
            val pos = items.indexOfFirst { it.id == old }
            if (pos >= 0) notifyItemChanged(pos)
        }
        val newPos = items.indexOfFirst { it.id == id }
        if (newPos >= 0) notifyItemChanged(newPos)
    }
}
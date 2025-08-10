package com.with_runn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.data.region.RegionResponse
import com.with_runn.databinding.ItemRegionBinding


class LocationSetAdapter (
    private val onItemClick: (RegionResponse) -> Unit,
) : RecyclerView.Adapter<LocationSetAdapter.ViewHolder>() {

    private val items: MutableList<RegionResponse> = mutableListOf()
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

                val oldId = selectedId
                selectedId = item.id

                // 이전/신규 포지션만 부분 갱신
                oldId?.let {
                    val oldPos = items.indexOfFirst { it.id == oldId }
                    if (oldPos >= 0) notifyItemChanged(oldPos)
                }
                notifyItemChanged(holder.bindingAdapterPosition)

                onItemClick(item)
            }
        }
    }


    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemRegionBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newList: List<RegionResponse>) {
        items.clear()
        selectedId = null
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun reset(){
        selectedId = null
        notifyDataSetChanged()
    }
}
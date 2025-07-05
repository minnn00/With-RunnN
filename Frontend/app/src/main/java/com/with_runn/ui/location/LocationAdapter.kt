package com.with_runn.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemLocationBinding

class LocationAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<String, LocationAdapter.LocationViewHolder>(LocationDiffCallback) {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class LocationViewHolder(
        private val binding: ItemLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previousSelectedPosition = selectedPosition
                    selectedPosition = position
                    
                    // 이전 선택된 아이템과 현재 선택된 아이템을 업데이트
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
                    
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(locationName: String, isSelected: Boolean) {
            binding.apply {
                tvLocationName.text = locationName
                ivSelected.visibility = if (isSelected) View.VISIBLE else View.GONE
            }
        }
    }

    fun clearSelection() {
        val previousSelectedPosition = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        notifyItemChanged(previousSelectedPosition)
    }

    override fun submitList(list: List<String>?) {
        // 새로운 리스트가 제출되면 선택 상태 초기화
        selectedPosition = RecyclerView.NO_POSITION
        super.submitList(list)
    }

    object LocationDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
} 
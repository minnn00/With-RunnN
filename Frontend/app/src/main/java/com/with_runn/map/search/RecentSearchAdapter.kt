package com.with_runn.map.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemRecentSearchBinding

class RecentSearchAdapter(
    private val onClick: (String) -> Unit,
    private val onDelete: (Int) -> Unit
) : ListAdapter<String, RecentSearchAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.apply{
            ItemRecentSearchBinding.recentSearchText.text = item
            ItemRecentSearchBinding.recentSearchText.setOnClickListener { onClick(item) }
            ItemRecentSearchBinding.recentSearchDelete.setOnClickListener {
                val pos = holder.bindingAdapterPosition

                if(pos != RecyclerView.NO_POSITION){
                    onDelete(pos)
                }
            }
        }
    }

    inner class ViewHolder(val binding: ItemRecentSearchBinding) : RecyclerView.ViewHolder(binding.root)

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<String>(){
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        }
    }
}
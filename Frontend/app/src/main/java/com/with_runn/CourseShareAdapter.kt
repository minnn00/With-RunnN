package com.with_runn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemCourseShareBinding
import android.view.View



class CourseShareAdapter(
    private val onItemSelected: (ShareTarget?) -> Unit
) : ListAdapter<ShareTarget, CourseShareAdapter.ViewHolder>(diffUtil) {

    private var selectedItem: ShareTarget? = null

    inner class ViewHolder(private val binding: ItemCourseShareBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShareTarget) {
            binding.imageProfile.setImageResource(item.imageResId)
            binding.textName.text = item.name
            binding.imageCheck.visibility = if (item.isSelected) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                val prevSelected = selectedItem
                if (prevSelected != item) {
                    prevSelected?.isSelected = false
                    item.isSelected = true
                    selectedItem = item
                    onItemSelected(item)
                    notifyDataSetChanged()
                } else {
                    item.isSelected = false
                    selectedItem = null
                    onItemSelected(null)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCourseShareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ShareTarget>() {
            override fun areItemsTheSame(oldItem: ShareTarget, newItem: ShareTarget): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: ShareTarget, newItem: ShareTarget): Boolean =
                oldItem == newItem
        }
    }
}




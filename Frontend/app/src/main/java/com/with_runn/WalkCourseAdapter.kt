package com.with_runn

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemWalkCourseMoreBinding

class WalkCourseAdapter(private val items: List<WalkCourse>) :
    RecyclerView.Adapter<WalkCourseAdapter.WalkCourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkCourseViewHolder {
        val binding = ItemWalkCourseMoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WalkCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalkCourseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class WalkCourseViewHolder(private val binding: ItemWalkCourseMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: WalkCourse) {
            binding.imageCourse.setImageResource(course.imageResId)
            binding.textTitle.text = course.title

            // 기존 tagFlow → layoutTags 로 변경
            binding.layoutTags.removeAllViews()

            course.tags.forEach { tag ->
                val tagView = LayoutInflater.from(binding.root.context).inflate(
                    R.layout.item_tag, binding.layoutTags, false
                ) as TextView
                tagView.text = tag // "#" 포함해서 넘겼으면 prefix 제거
                binding.layoutTags.addView(tagView)
            }
        }
    }
}

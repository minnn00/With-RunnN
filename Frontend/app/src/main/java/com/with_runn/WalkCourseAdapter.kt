package com.with_runn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemWalkCourseMoreBinding
import com.with_runn.WalkCourse

class WalkCourseAdapter(
    private val items: List<WalkCourse>,
    private val onItemClick: (WalkCourse) -> Unit  // 클릭 콜백 추가
) : RecyclerView.Adapter<WalkCourseAdapter.WalkCourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkCourseViewHolder {
        val binding = ItemWalkCourseMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            binding.textCourseName.text = course.title
            binding.tag1.text = course.tags.getOrNull(0) ?: ""
            binding.tag2.text = course.tags.getOrNull(1) ?: ""
            binding.distanceText.text = course.distance
            binding.timeText.text = course.time

            binding.root.setOnClickListener {
                onItemClick(course)
            }
        }
    }
}

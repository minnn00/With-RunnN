package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.data.WalkCourse
import com.with_runn.databinding.ItemWalkCourseMoreBinding
import com.bumptech.glide.Glide
import com.with_runn.R

class WalkCourseAdapter(
    private var items: List<WalkCourse>,
    private val onItemClick: (WalkCourse) -> Unit
) : RecyclerView.Adapter<WalkCourseAdapter.WalkCourseViewHolder>() {

    fun updateItems(newItems: List<WalkCourse>) {
        items = newItems
        notifyDataSetChanged()
    }

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
            // Glide로 서버 이미지 또는 기본 이미지 적용
            Glide.with(binding.root)
                .load(course.imageUrl ?: R.drawable.image)
                .into(binding.imageCourse)

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

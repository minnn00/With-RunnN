package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.course.CourseSummary
import com.with_runn.databinding.LocalCourseBinding

class LocalCourseAdapter(
    private val courseList: MutableList<CourseSummary>,
    private val onItemClick: (CourseSummary) -> Unit
) : RecyclerView.Adapter<LocalCourseAdapter.LocalCourseViewHolder>() {

    inner class LocalCourseViewHolder(val binding: LocalCourseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalCourseViewHolder {
        val binding = LocalCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocalCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocalCourseViewHolder, position: Int) {
        val course = courseList[position]
        with(holder.binding) {
            titleText.text = course.name

            val firstTag = course.keyword.orEmpty().firstOrNull().orEmpty()
            tagText.text = if (firstTag.isNotBlank()) "#$firstTag" else ""
            tagText.visibility = if (firstTag.isBlank()) View.GONE else View.VISIBLE

            val safeUrl = course.courseImage
                ?.takeIf { it.isNotBlank() && it != "null" }

            Glide.with(root.context)
                .load(safeUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_fallback)
                .fallback(R.drawable.ic_new_logo_gray)
                .error(R.drawable.ic_new_logo_gray)
                .into(imageCourse)

            root.setOnClickListener { onItemClick(course) }
        }
    }


    override fun getItemCount(): Int = courseList.size

    fun updateData(newList: List<CourseSummary>, limit: Int? = null) {
        val data = limit?.let { newList.take(it) } ?: newList
        courseList.clear()
        courseList.addAll(data)
        notifyDataSetChanged()
    }
}

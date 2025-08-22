package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.course.CourseSummary
import com.with_runn.databinding.HotCourseBinding

class HotCourseAdapter(
    hotCourses: List<CourseSummary>,
    private val onItemClick: (CourseSummary) -> Unit
) : RecyclerView.Adapter<HotCourseAdapter.HotCourseViewHolder>() {

    private val courseList = hotCourses.toMutableList()

    inner class HotCourseViewHolder(val binding: HotCourseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotCourseViewHolder {
        val binding = HotCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HotCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HotCourseViewHolder, position: Int) {
        val course = courseList[position]
        with(holder.binding) {
            val safeUrl = course.courseImage
                ?.takeIf { it.isNotBlank() && it != "null" }

            Glide.with(root.context)
                .load(safeUrl)
                .placeholder(R.drawable.ic_fallback)
                .fallback(R.drawable.ic_new_logo_gray)
                .error(R.drawable.ic_new_logo_gray)
                .centerCrop()
                .into(imageCourse)

            titleText.text = course.name
            timeText.text = "${course.time}분"

            val tags = course.keyword.orEmpty()
            val t1 = tags.getOrNull(0).orEmpty()
            val t2 = tags.getOrNull(1).orEmpty()

            tag1.text = if (t1.isNotBlank()) "#$t1" else ""
            tag1.visibility = if (t1.isBlank()) View.GONE else View.VISIBLE

            tag2.text = if (t2.isNotBlank()) "#$t2" else ""
            tag2.visibility = if (t2.isBlank()) View.GONE else View.VISIBLE

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

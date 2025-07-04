package com.with_runn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class HotCourse(
    val imageRes: Int,
    val title: String,
    val tags: List<String>
)

class HotCourseAdapter(private val hotCourses: List<HotCourse>) :
    RecyclerView.Adapter<HotCourseAdapter.HotCourseViewHolder>() {

    inner class HotCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCourse: ImageView = itemView.findViewById(R.id.image_course)
        val titleText: TextView = itemView.findViewById(R.id.title_text)
        val tag1: TextView = itemView.findViewById(R.id.tag1)
        val tag2: TextView = itemView.findViewById(R.id.tag2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotCourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hot_course, parent, false)
        return HotCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotCourseViewHolder, position: Int) {
        val course = hotCourses[position]
        holder.imageCourse.setImageResource(course.imageRes)
        holder.titleText.text = course.title
        holder.tag1.text = course.tags.getOrNull(0) ?: ""
        holder.tag2.text = course.tags.getOrNull(1) ?: ""
    }

    override fun getItemCount(): Int = hotCourses.size
}
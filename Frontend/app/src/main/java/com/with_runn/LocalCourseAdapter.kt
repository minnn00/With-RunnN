package com.with_runn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class LocalCourse(
    val imageRes: Int,
    val tag: String,
    val title: String
)

class LocalCourseAdapter(private val courseList: List<LocalCourse>) :
    RecyclerView.Adapter<LocalCourseAdapter.LocalCourseViewHolder>() {

    inner class LocalCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCourse: ImageView = itemView.findViewById(R.id.image_course)
        val tagText: TextView = itemView.findViewById(R.id.tag_text)
        val titleText: TextView = itemView.findViewById(R.id.title_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalCourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.local_course, parent, false)
        return LocalCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalCourseViewHolder, position: Int) {
        val course = courseList[position]
        holder.imageCourse.setImageResource(course.imageRes)
        holder.tagText.text = course.tag
        holder.titleText.text = course.title
    }

    override fun getItemCount(): Int = courseList.size
}
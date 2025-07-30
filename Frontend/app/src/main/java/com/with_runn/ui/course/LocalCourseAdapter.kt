package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.data.LocalCourse
import com.with_runn.R

class LocalCourseAdapter(
    private val courseList: MutableList<LocalCourse>,
    private val onItemClick: (LocalCourse) -> Unit
) : RecyclerView.Adapter<LocalCourseAdapter.LocalCourseViewHolder>() {

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

        holder.itemView.setOnClickListener {
            onItemClick(course)
        }
    }

    override fun getItemCount(): Int = courseList.size

    fun updateData(newList: List<LocalCourse>) {
        courseList.clear()
        courseList.addAll(newList)
        notifyDataSetChanged()
    }
}
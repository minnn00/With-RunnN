package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.with_runn.R

data class HotCourse(
    val imageRes: Int,
    val title: String,
    val tags: List<String>,
    val distance: String,
    val time: String
)


class HotCourseAdapter(
    private val hotCourses: List<HotCourse>,
    private val onItemClick: (HotCourse) -> Unit
) : RecyclerView.Adapter<HotCourseAdapter.HotCourseViewHolder>() {

    inner class HotCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCourse: ImageView = itemView.findViewById(R.id.image_course)
        val titleText: TextView = itemView.findViewById(R.id.text_course_name)  // 여기 수정!
        val tag1: TextView = itemView.findViewById(R.id.tag1)
        val tag2: TextView = itemView.findViewById(R.id.tag2)
        val distanceText: TextView = itemView.findViewById(R.id.distance_text)
        val timeText: TextView = itemView.findViewById(R.id.time_text)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotCourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_walk_course_more, parent, false)
        return HotCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotCourseViewHolder, position: Int) {
        val course = hotCourses[position]
        holder.imageCourse.setImageResource(course.imageRes)
        holder.titleText.text = course.title
        holder.tag1.text = course.tags.getOrNull(0) ?: ""
        holder.tag2.text = course.tags.getOrNull(1) ?: ""
        holder.distanceText.text = course.distance
        holder.timeText.text = course.time

        holder.itemView.setOnClickListener {
            onItemClick(course)
        }
    }

    override fun getItemCount(): Int = hotCourses.size
}
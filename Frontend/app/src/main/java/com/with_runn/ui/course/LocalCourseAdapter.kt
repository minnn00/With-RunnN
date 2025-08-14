package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.data.LocalCourse
import com.with_runn.R
import com.bumptech.glide.Glide

class LocalCourseAdapter(
    private val courseList: MutableList<LocalCourse>,
    private val onItemClick: (LocalCourse) -> Unit
) : RecyclerView.Adapter<LocalCourseAdapter.LocalCourseViewHolder>() {

    inner class LocalCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCourse: ImageView = itemView.findViewById(R.id.image_course)
        val tagText: TextView      = itemView.findViewById(R.id.tag_text)
        val titleText: TextView    = itemView.findViewById(R.id.title_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalCourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.local_course, parent, false)
        return LocalCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalCourseViewHolder, position: Int) {
        val course = courseList[position]

        // 제목
        holder.titleText.text = course.title

        // 태그(없으면 감추기)
        holder.tagText.text = course.tag
        holder.tagText.visibility = if (course.tag.isBlank()) View.GONE else View.VISIBLE

        // 이미지: url 있으면 로드, 없으면 기본 이미지
        val ctx = holder.itemView.context
        if (!course.imageUrl.isNullOrBlank()) {
            Glide.with(ctx)
                .load(course.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.image)
                .error(R.drawable.image)
                .into(holder.imageCourse)
        } else {
            holder.imageCourse.setImageResource(course.imageRes)
        }

        holder.itemView.setOnClickListener { onItemClick(course) }
    }

    override fun getItemCount(): Int = courseList.size

    fun updateData(newList: List<LocalCourse>) {
        courseList.clear()
        courseList.addAll(newList)
        notifyDataSetChanged()
    }
}

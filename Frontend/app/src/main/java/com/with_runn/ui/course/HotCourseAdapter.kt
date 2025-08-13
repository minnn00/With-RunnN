package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.data.HotCourse

class HotCourseAdapter(
    hotCourses: List<HotCourse>,
    private val onItemClick: (HotCourse) -> Unit
) : RecyclerView.Adapter<HotCourseAdapter.HotCourseViewHolder>() {

    private val courseList = hotCourses.toMutableList()

    fun updateData(newList: List<HotCourse>) {
        courseList.clear()
        courseList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class HotCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCourse: ImageView = itemView.findViewById(R.id.image_course)
        val titleText: TextView = itemView.findViewById(R.id.text_course_name)
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
        val course = courseList[position]

        // 1) 이미지: URL이 있으면 Glide, 없으면 기본 이미지로 채움(레이아웃 깨지지 않게)
        val url = course.imageUrl?.takeIf { it.isNotBlank() && it != "null" }
        if (url != null) {
            holder.imageCourse.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(url)
                .placeholder(R.drawable.image)
                .error(R.drawable.image)
                .centerCrop()
                .into(holder.imageCourse)
        } else {
            holder.imageCourse.visibility = View.VISIBLE
            holder.imageCourse.setImageResource(R.drawable.image)
        }

        // 2) 공통 텍스트
        holder.titleText.text = course.title
        holder.distanceText.text = course.distance      // 예: "0km" / "1.3km"
        holder.timeText.text = course.time              // 예: "30분"

        // 3) 태그(최대 2개, '#'(있으면) 제거)
        val t1 = course.tags.getOrNull(0)?.removePrefix("#").orEmpty()
        val t2 = course.tags.getOrNull(1)?.removePrefix("#").orEmpty()
        holder.tag1.text = t1
        holder.tag1.visibility = if (t1.isBlank()) View.GONE else View.VISIBLE
        holder.tag2.text = t2
        holder.tag2.visibility = if (t2.isBlank()) View.GONE else View.VISIBLE

        // 4) 클릭
        holder.itemView.setOnClickListener { onItemClick(course) }
    }

    override fun getItemCount(): Int = courseList.size
}

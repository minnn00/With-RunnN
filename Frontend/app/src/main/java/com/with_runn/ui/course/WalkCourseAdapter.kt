package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.with_runn.R
import com.with_runn.data.WalkCourse
import com.with_runn.databinding.ItemWalkCourseMoreBinding

class WalkCourseAdapter(
    private var items: List<WalkCourse>,
    private val onItemClick: (WalkCourse) -> Unit
) : RecyclerView.Adapter<WalkCourseAdapter.WalkCourseViewHolder>() {

    fun updateItems(newItems: List<WalkCourse>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkCourseViewHolder {
        val binding = ItemWalkCourseMoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WalkCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalkCourseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class WalkCourseViewHolder(private val binding: ItemWalkCourseMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: WalkCourse) = with(binding) {
            // 이미지: URL이 있으면 Glide, 없으면 기본 이미지
            val url = course.imageUrl
            if (!url.isNullOrBlank()) {
                imageCourse.visibility = View.VISIBLE
                Glide.with(root.context)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.image)   // 로딩 중
                    .error(R.drawable.image)         // 실패 시
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(imageCourse)
            } else {
                imageCourse.visibility = View.VISIBLE
                imageCourse.setImageResource(R.drawable.image)
            }

            // 텍스트
            textCourseName.text = course.title
            distanceText.text   = course.distance
            timeText.text       = course.time   // "30분" 형태

            // 태그: 최대 2개, 없으면 숨김
            val t1 = course.tags.getOrNull(0)?.removePrefix("#").orEmpty()
            val t2 = course.tags.getOrNull(1)?.removePrefix("#").orEmpty()
            tag1.text = t1
            tag1.visibility = if (t1.isEmpty()) View.GONE else View.VISIBLE
            tag2.text = t2
            tag2.visibility = if (t2.isEmpty()) View.GONE else View.VISIBLE

            root.setOnClickListener { onItemClick(course) }
        }
    }
}

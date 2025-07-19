package com.with_runn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemWalkCourseMoreBinding

class MyPageCourseAdapter(
    private var tabType: TabType,
    private var isDeleteMode: Boolean = false,
    private val onItemDeleteClick: (WalkCourse) -> Unit,
    private val onItemClicked: (WalkCourse) -> Unit,
    private val onScrapClick: (WalkCourse) -> Unit,
    private val onLikeClick: (WalkCourse) -> Unit
) : RecyclerView.Adapter<MyPageCourseAdapter.ViewHolder>() {

    private val items = mutableListOf<WalkCourse>()

    inner class ViewHolder(private val binding: ItemWalkCourseMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkCourse) = with(binding) {
            imageCourse.setImageResource(item.imageResId)
            textCourseName.text = item.title
            distanceText.text = item.distance
            timeText.text = item.time

            // 태그 2개까지 표시
            if (item.tags.size >= 2) {
                tag1.text = item.tags[0]
                tag2.text = item.tags[1]
                tag2.visibility = View.VISIBLE
            } else {
                tag1.text = item.tags.getOrNull(0) ?: ""
                tag2.visibility = View.GONE
            }

            iconCancle.visibility = View.GONE
            iconScrap.visibility = View.GONE
            iconHeart.visibility = View.GONE

            when (tabType) {
                TabType.MY_COURSE -> {
                    if (isDeleteMode) {
                        iconCancle.visibility = View.VISIBLE
                        iconCancle.setOnClickListener { onItemDeleteClick(item) }
                    }
                    root.setOnClickListener {
                        if (!isDeleteMode) onItemClicked(item)
                    }
                }
                TabType.SCRAP -> {
                    iconScrap.visibility = View.VISIBLE
                    iconScrap.setOnClickListener { onScrapClick(item) }
                    root.setOnClickListener { onItemClicked(item) }
                }
                TabType.LIKE -> {
                    iconHeart.visibility = View.VISIBLE
                    iconHeart.setOnClickListener { onLikeClick(item) }
                    root.setOnClickListener { onItemClicked(item) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWalkCourseMoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<WalkCourse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setTabType(type: TabType, deleteMode: Boolean) {
        tabType = type
        isDeleteMode = deleteMode
        notifyDataSetChanged()
    }
}
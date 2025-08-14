package com.with_runn

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.data.WalkCourse
import com.with_runn.databinding.ItemWalkCourseMoreBinding
import com.with_runn.ui.course.TabType


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

            // 1) 이미지 (URL > resId > 없음) + Glide 로딩 로그
            when {
                !item.imageUrl.isNullOrBlank() -> {
                    imageCourse.visibility = View.VISIBLE
                    Log.d("IMG", "try load url=${item.imageUrl}")
                    Glide.with(root.context)
                        .load(item.imageUrl)
                        .centerCrop()
                        .addListener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {

                            override fun onLoadFailed(
                                e: com.bumptech.glide.load.engine.GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>, // ❗ non-null
                                isFirstResource: Boolean
                            ): Boolean {
                                android.util.Log.e("Glide", "load failed url=$model, err=${e?.localizedMessage}", e)
                                return false // false로 해야 error() / placeholder가 적용
                            }

                            override fun onResourceReady(
                                resource: android.graphics.drawable.Drawable,   // non-null
                                model: Any,
                                target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>, // ❗ non-null
                                dataSource: com.bumptech.glide.load.DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                android.util.Log.d("Glide", "loaded url=$model from=$dataSource")
                                return false // false로 해야 into(imageView) 진행
                            }
                        })
                        .into(imageCourse)
                }
                item.imageResId != 0 -> {
                    imageCourse.visibility = View.VISIBLE
                    imageCourse.setImageResource(item.imageResId)
                }
                else -> {
                    imageCourse.visibility = View.GONE
                }
            }

            // 2) 텍스트
            textCourseName.text = item.title
            distanceText.text   = item.distance
            timeText.text       = item.time

            // 3) 태그(최대 2개, 앞의 # 제거)
            val t1 = item.tags.getOrNull(0)?.removePrefix("#").orEmpty()
            val t2 = item.tags.getOrNull(1)?.removePrefix("#").orEmpty()
            tag1.text = t1
            tag1.visibility = if (t1.isBlank()) View.GONE else View.VISIBLE
            tag2.text = t2
            tag2.visibility = if (t2.isBlank()) View.GONE else View.VISIBLE

            // 4) 아이콘/클릭 초기화
            iconCancle.visibility = View.GONE
            iconScrap.visibility  = View.GONE
            iconHeart.visibility  = View.GONE
            iconCancle.setOnClickListener(null)
            iconScrap.setOnClickListener(null)
            iconHeart.setOnClickListener(null)
            root.setOnClickListener(null)

            // 5) 탭별 동작
            when (tabType) {
                TabType.MY_COURSE -> {
                    if (isDeleteMode) {
                        iconCancle.visibility = View.VISIBLE
                        iconCancle.setOnClickListener {
                            val pos = bindingAdapterPosition
                            if (pos != RecyclerView.NO_POSITION) {
                                val removed = items.removeAt(pos)
                                notifyItemRemoved(pos)
                                notifyItemRangeChanged(pos, items.size - pos)
                                onItemDeleteClick(removed)
                            }
                        }
                    }
                    root.setOnClickListener { if (!isDeleteMode) onItemClicked(item) }
                }

                TabType.SCRAP -> {
                    iconScrap.visibility = View.VISIBLE
                    ImageViewCompat.setImageTintList(
                        iconScrap,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(root.context, R.color.green_700)
                        )
                    )
                    iconScrap.setOnClickListener {
                        val pos = bindingAdapterPosition
                        if (pos != RecyclerView.NO_POSITION) {
                            val removed = items.removeAt(pos)
                            notifyItemRemoved(pos)
                            notifyItemRangeChanged(pos, items.size - pos)
                            onScrapClick(removed)
                        }
                    }
                    root.setOnClickListener { onItemClicked(item) }
                }

                TabType.LIKE -> {
                    iconHeart.visibility = View.VISIBLE
                    iconHeart.setOnClickListener {
                        val pos = bindingAdapterPosition
                        if (pos != RecyclerView.NO_POSITION) {
                            val removed = items.removeAt(pos)
                            notifyItemRemoved(pos)
                            notifyItemRangeChanged(pos, items.size - pos)
                            onLikeClick(removed)
                        }
                    }
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

    fun removeItemById(id: Int) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx != -1) {
            items.removeAt(idx)
            notifyItemRemoved(idx)
            notifyItemRangeChanged(idx, items.size - idx)
        }
    }
}

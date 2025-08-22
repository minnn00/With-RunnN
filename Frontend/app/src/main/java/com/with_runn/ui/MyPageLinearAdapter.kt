package com.with_runn.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.mypage.CourseBrief

class MyPageLinearAdapter(
    private val onItemClick: (CourseBrief) -> Unit,
    private val onEditClick: (CourseBrief) -> Unit,
    private val onDeleteClick: (CourseBrief) -> Unit
) : ListAdapter<CourseBrief, MyPageLinearAdapter.ViewHolder>(DIFF) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mypage_linear, parent, false)
        return ViewHolder(v, onItemClick, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val onItemClick: (CourseBrief) -> Unit,
        private val onEditClick: (CourseBrief) -> Unit,
        private val onDeleteClick: (CourseBrief) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val image: ImageView = itemView.findViewById(R.id.image_course)
        private val layoutTags: LinearLayout = itemView.findViewById(R.id.layout_tags)
        private val tag1: TextView = itemView.findViewById(R.id.tag1)
        private val tag2: TextView = itemView.findViewById(R.id.tag2)
        private val title: TextView = itemView.findViewById(R.id.title_text)
        private val timeText: TextView = itemView.findViewById(R.id.time_text)
        private val actionBtn: ImageButton = itemView.findViewById(R.id.action_btn)

        fun bind(item: CourseBrief) {
            // 제목
            title.text = item.courseName.ifBlank { "제목 없음" }

            // 시간
            if (item.time > 0) {
                timeText.text = "${item.time}분"
                timeText.isVisible = true
            } else {
                timeText.isGone = true
            }

            // 태그 (keyword 원문 파싱)
            val tags = parseKeyword(item.keyword)
            if (tags.isEmpty()) {
                layoutTags.isGone = true
            } else {
                layoutTags.isVisible = true
                tag1.isVisible = tags.isNotEmpty()
                tag2.isVisible = tags.size >= 2
                if (tags.isNotEmpty()) tag1.text = "#${tags[0]}"
                if (tags.size >= 2) tag2.text = "#${tags[1]}"
            }

            // 이미지 (placeholder/fallback/error는 필요시 체인에 추가해서 사용해줘)
            val url = item.courseImage?.takeIf { it.isNotBlank() }
            if (url != null) {
                Glide.with(image)
                    .load(url)
                    .placeholder(R.drawable.ic_fallback)
                    .error(R.drawable.ic_new_logo_gray)
                    .fallback(R.drawable.ic_new_logo_gray)
                    .centerCrop()
                    .into(image)
            } else {
                image.setImageResource(R.drawable.ic_new_logo_gray)
            }

            // 클릭
            itemView.setOnClickListener { onItemClick(item) }
            // 액션 버튼 -> 팝업 메뉴(수정/삭제)
            actionBtn.setOnClickListener { v ->
                val themedCtx = ContextThemeWrapper(v.context, R.style.MyPopupMenu)
                val pm = PopupMenu(v.context, v)
                pm.menu.add(0, MENU_EDIT, 0, "수정")
                pm.menu.add(0, MENU_DELETE, 1, "삭제")
                pm.setOnMenuItemClickListener { mi ->
                    when (mi.itemId) {
                        MENU_EDIT -> { onEditClick(item); true }
                        MENU_DELETE -> { onDeleteClick(item); true }
                        else -> false
                    }
                }
                pm.show()
            }
        }

        /** 서버가 keyword를 여러 포맷으로 줄 수 있어 안전하게 파싱 */
        private fun parseKeyword(raw: String?): List<String> {
            if (raw.isNullOrBlank()) return emptyList()
            val s = raw.trim()

            // JSON 배열 스타일: ["a","b"]
            if (s.startsWith("[") && s.endsWith("]")) {
                return s.substring(1, s.length - 1)
                    .split(',')
                    .map { it.trim().trim('"', '“', '”', '\'') }
                    .filter { it.isNotBlank() }
            }
            // 쉼표 구분: "a,b"
            if (s.contains(",")) {
                return s.split(',')
                    .map { it.trim().trim('"', '“', '”', '\'') }
                    .filter { it.isNotBlank() }
            }
            // 단일 값
            return listOf(s.trim().trim('"', '“', '”', '\''))
        }
    }

    companion object {
        private const val MENU_EDIT = 1001
        private const val MENU_DELETE = 1002

        private val DIFF = object : DiffUtil.ItemCallback<CourseBrief>() {
            override fun areItemsTheSame(oldItem: CourseBrief, newItem: CourseBrief): Boolean =
                oldItem.courseId == newItem.courseId
            override fun areContentsTheSame(oldItem: CourseBrief, newItem: CourseBrief): Boolean =
                oldItem == newItem
        }
    }
}

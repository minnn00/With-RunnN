package com.with_runn.ui.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R
import com.with_runn.data.model.Notice

class NoticeAdapter(private var noticeList: List<Notice>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LIKE = 0
        private const val TYPE_FOLLOW = 1
        private const val TYPE_SCRAP = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (noticeList[position].noticeType) {
            "LIKE" -> TYPE_LIKE
            "FOLLOW" -> TYPE_FOLLOW
            "SCRAP" -> TYPE_SCRAP
            else -> TYPE_LIKE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LIKE -> LikeViewHolder(inflater.inflate(R.layout.item_notice_like, parent, false))
            TYPE_FOLLOW -> FollowViewHolder(inflater.inflate(R.layout.item_notice_follow, parent, false))
            TYPE_SCRAP -> ScrapViewHolder(inflater.inflate(R.layout.item_notice_scrap, parent, false))
            else -> LikeViewHolder(inflater.inflate(R.layout.item_notice_like, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notice = noticeList[position]
        when (holder) {
            is LikeViewHolder -> holder.bind(notice)
            is FollowViewHolder -> holder.bind(notice)
            is ScrapViewHolder -> holder.bind(notice)
        }
    }

    override fun getItemCount(): Int = noticeList.size

    fun updateData(newList: List<Notice>) {
        noticeList = newList
        notifyDataSetChanged()
    }

    class LikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(notice: Notice) {
//            itemView.findViewById<TextView>(R.id.noticeText).text = notice.message
        }
    }

    class FollowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(notice: Notice) {
//            itemView.findViewById<TextView>(R.id.noticeText).text = notice.message
        }
    }

    class ScrapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(notice: Notice) {
//            itemView.findViewById<TextView>(R.id.noticeText).text = notice.message
        }
    }
}


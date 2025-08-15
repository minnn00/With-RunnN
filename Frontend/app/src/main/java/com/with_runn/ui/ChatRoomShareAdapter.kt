package com.with_runn.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.ItemChatRoomBinding
import com.with_runn.ui.chat.model.ChatRoom

private object RoomDiff : DiffUtil.ItemCallback<ChatRoom>() {
    override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom) = oldItem.chatId == newItem.chatId
    override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom) = oldItem == newItem
}

class ChatRoomShareAdapter(
    private val onClick: (ChatRoom) -> Unit
) : ListAdapter<ChatRoom, ChatRoomShareAdapter.VH>(RoomDiff) {

    inner class VH(val b: ItemChatRoomBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemChatRoomBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        Log.d("ShareAdapter", "bind#$position id=${item.chatId} name=${item.name}")
        Log.d("ShareAdapter", "item#${position} size=${holder.b.root.width}x${holder.b.root.height}")
        holder.b.roomName.text = item.name
        holder.itemView.setOnClickListener { onClick(item) }
    }
}

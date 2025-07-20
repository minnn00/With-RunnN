package com.with_runn.ui.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.databinding.PlacePopupBinding

class PlacePopupAdapter(
    private val items: MutableList<PlaceItem>, // List → MutableList
    private val onCloseClick: (Int) -> Unit
) : RecyclerView.Adapter<PlacePopupAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(val binding: PlacePopupBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlacePopupBinding.inflate(inflater, parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvPlaceTitle.text = item.type
        holder.binding.tvPlaceComment.text = item.comment
        holder.binding.imgIcon.setImageResource(item.iconResId)

        holder.binding.btnClose.setOnClickListener {
            onCloseClick(position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}

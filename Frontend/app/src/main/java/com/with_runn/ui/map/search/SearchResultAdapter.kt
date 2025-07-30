package com.with_runn.ui.map.search

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.databinding.ItemSearchResultBinding
import com.with_runn.formatToHHMM

data class SearchResultItem(
    val placeId: String,
    val name: String,
    val isOpen: Boolean,
    val openTime: Int,
    val closeTime: Int,
    val isParkable: Boolean,
    val imageUri: String? = null,

    val latitude: Double,
    val longitude: Double
) : java.io.Serializable

class SearchResultAdapter(
    private val onItemClick: (SearchResultItem) -> Unit
) : ListAdapter<SearchResultItem, SearchResultAdapter.ViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<SearchResultItem>() {
        override fun areItemsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
            return oldItem.placeId == newItem.placeId
        }

        override fun areContentsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(val binding: ItemSearchResultBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchResultItem) {
            val uri = item.imageUri?.toUri()

            binding.apply {
                if (uri != null) {
                    Glide.with(placeImage.context)
                        .load(uri)
                        .centerCrop()
                        .error(R.drawable.ic_logo)
                        .into(placeImage)
                } else {
                    placeImage.setImageResource(R.drawable.ic_logo)
                }

                placeName.text = item.name
                placeServiceState.text = root.context.getString(
                    if (item.isOpen) R.string.place_service_available else R.string.place_service_inavailable
                )
                placeServiceTime.text = "${formatToHHMM(item.openTime)} ~ ${formatToHHMM(item.closeTime)}"

                if (item.isParkable) {
                    placeParkState.apply {
                        text = root.context.getString(R.string.park_able)
                        setTextColor(root.context.getColor(R.color.blue_500))
                        backgroundTintList = ColorStateList.valueOf(root.context.getColor(R.color.blue_050))
                    }
                } else {
                    placeParkState.apply {
                        text = root.context.getString(R.string.park_inable)
                        setTextColor(root.context.getColor(R.color.red_500))
                        backgroundTintList = ColorStateList.valueOf(root.context.getColor(R.color.red_050))
                    }
                }

                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

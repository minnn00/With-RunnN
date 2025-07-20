package com.with_runn.ui.map.search

import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R
import com.with_runn.databinding.ItemSearchResultBinding
import com.with_runn.formatToHHMM

data class SearchResultItem(
    val name: String,
    val isOpen: Boolean,
    val openTime : Int,
    val closeTime : Int,
    val isParkable: Boolean,
    val imageUri: Uri? = null
)

class SearchResultAdapter(
    private val items: List<SearchResultItem>,
    private val onItemClick: (SearchResultItem) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemSearchResultBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: SearchResultItem){
            binding.apply{
                // TODO: Glide로 이미지 삽입
                placeName.text = item.name
                placeServiceState.text = root.context.getString(
                    if(item.isOpen) R.string.place_service_available else R.string.place_service_inavailable
                )
                placeServiceTime.text = "${formatToHHMM(item.openTime)} ~ ${formatToHHMM(item.closeTime)}"
                if(item.isParkable){
                    placeParkState.apply{
                        text = root.context.getString(R.string.park_able)
                        setTextColor(root.context.getColor(R.color.blue_500))
                        placeParkState.backgroundTintList = ColorStateList.valueOf(root.context.getColor(
                            R.color.blue_050))
                    }
                }else{
                    placeParkState.apply{
                        text = root.context.getString(R.string.park_inable)
                        setTextColor(root.context.getColor(R.color.red_500))
                        placeParkState.backgroundTintList = ColorStateList.valueOf(root.context.getColor(
                            R.color.red_050))
                    }
                }

                root.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
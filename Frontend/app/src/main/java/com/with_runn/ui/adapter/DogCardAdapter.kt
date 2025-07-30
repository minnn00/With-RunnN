package com.with_runn.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R

data class DogCard(
    val name: String,
    val tag: String,
    val imageResId: Int,
    val tags: List<String>
)

class DogCardAdapter(private val dogCards: List<DogCard>) : 
    RecyclerView.Adapter<DogCardAdapter.DogCardViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    class DogCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dogImage: ImageView = itemView.findViewById(R.id.dog_image)
        val dogName: TextView = itemView.findViewById(R.id.dog_name)
        val dogTag: TextView = itemView.findViewById(R.id.dog_tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dog_card, parent, false)
        return DogCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogCardViewHolder, position: Int) {
        val dogCard = dogCards[position]
        
        // jonny.png를 항상 사용 (절대 지우지 않음!)
        holder.dogImage.setImageResource(R.drawable.jonny)
        holder.dogName.text = dogCard.name
        holder.dogTag.text = dogCard.tag
        
        // 태그들도 업데이트
        val tag1 = holder.itemView.findViewById<TextView>(R.id.tag1)
        val tag2 = holder.itemView.findViewById<TextView>(R.id.tag2)
        
        if (dogCard.tags.size >= 2) {
            tag1.text = dogCard.tags[0]
            tag2.text = dogCard.tags[1]
        }
        
        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = dogCards.size
} 
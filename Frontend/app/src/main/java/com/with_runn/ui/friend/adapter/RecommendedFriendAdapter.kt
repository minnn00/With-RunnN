package com.with_runn.ui.friend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.with_runn.R
import com.with_runn.ui.friend.model.dto.RecommendedFriendResponse

class RecommendedFriendAdapter(
    private var friends: List<RecommendedFriendResponse> = emptyList(),
    private val onItemClick: ((RecommendedFriendResponse) -> Unit)? = null,
    private val layoutType: LayoutType = LayoutType.Card
) : RecyclerView.Adapter<RecommendedFriendAdapter.ViewHolder>() {

    enum class LayoutType { Card, List }

    class ViewHolder(view: View, val layoutType: LayoutType) : RecyclerView.ViewHolder(view) {
        // Card layout views
        val dogImage: ImageView? = view.findViewById(R.id.dog_image)
        val dogName: TextView? = view.findViewById(R.id.dog_name)
        val personalityTag: TextView? = view.findViewById(R.id.personality_tag)
        val firstPersonalityTag: TextView? = view.findViewById(R.id.first_personality_tag)
        val secondPersonalityTag: TextView? = view.findViewById(R.id.second_personality_tag)

        // List layout views
        val profileImage: ImageView? = view.findViewById(R.id.profileImage)
        val userName: TextView? = view.findViewById(R.id.userName)
        val styleTags: TextView? = view.findViewById(R.id.styleTags)
        val characterTags: TextView? = view.findViewById(R.id.characterTags)
        val commonTags: TextView? = view.findViewById(R.id.commonTags)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutRes = when (layoutType) {
            LayoutType.Card -> R.layout.item_friend_card
            LayoutType.List -> R.layout.item_recommended_friend
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(view, layoutType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]

        if (holder.layoutType == LayoutType.Card) {
            // Card binding
            holder.dogName?.text = friend.userName ?: ""

            val profileImageUrl = friend.profileImage
            if (!profileImageUrl.isNullOrEmpty() && profileImageUrl != "example.url") {
                holder.dogImage?.let {
                    Glide.with(it.context)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .centerCrop()
                        .into(it)
                }
            } else {
                holder.dogImage?.setImageResource(R.drawable.default_profile)
            }

            val styleText = friend.style?.firstOrNull() ?: ""
            holder.personalityTag?.text = "#$styleText"

            val allTags = (friend.style ?: emptyList()) + (friend.characters ?: emptyList())
            if (allTags.isNotEmpty()) {
                holder.firstPersonalityTag?.text = "#${allTags[0]}"
                holder.firstPersonalityTag?.visibility = View.VISIBLE
                if (allTags.size > 1) {
                    holder.secondPersonalityTag?.text = "#${allTags[1]}"
                    holder.secondPersonalityTag?.visibility = View.VISIBLE
                } else {
                    holder.secondPersonalityTag?.visibility = View.GONE
                }
            } else {
                holder.firstPersonalityTag?.visibility = View.GONE
                holder.secondPersonalityTag?.visibility = View.GONE
            }
        } else {
            // List binding
            holder.userName?.text = friend.userName ?: ""

            val styleList = friend.style ?: emptyList()
            val characterList = friend.characters ?: emptyList()
            holder.styleTags?.text = if (styleList.isNotEmpty()) styleList.joinToString(prefix = "#", separator = " #") else ""
            holder.characterTags?.text = if (characterList.isNotEmpty()) characterList.joinToString(prefix = "#", separator = " #") else ""

            val commonList = friend.common ?: emptyList()
            holder.commonTags?.text = if (commonList.isNotEmpty()) commonList.joinToString(prefix = "공통점: ", separator = ", ") else ""

            val profileImageUrl = friend.profileImage
            if (!profileImageUrl.isNullOrEmpty() && profileImageUrl != "example.url") {
                holder.profileImage?.let {
                    Glide.with(it.context)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .centerCrop()
                        .into(it)
                }
            } else {
                holder.profileImage?.setImageResource(R.drawable.default_profile)
            }
        }

        holder.itemView.setOnClickListener { onItemClick?.invoke(friend) }
    }

    override fun getItemCount() = friends.size

    fun updateFriends(newFriends: List<RecommendedFriendResponse>) {
        friends = newFriends
        notifyDataSetChanged()
    }
} 
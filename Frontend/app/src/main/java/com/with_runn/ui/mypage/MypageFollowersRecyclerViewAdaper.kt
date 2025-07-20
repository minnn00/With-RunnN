package com.with_runn.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R

class MypageFollowersRecyclerViewAdaper(private val profiles: List<String>) : RecyclerView.Adapter<MypageFollowersRecyclerViewAdaper.ProfileViewHolder>() {
        inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.name_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_mypage_follower_profile, parent, false)
            return ProfileViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
            val profile = profiles[position]
            holder.name.text = profile
        }

        override fun getItemCount(): Int = profiles.size
}

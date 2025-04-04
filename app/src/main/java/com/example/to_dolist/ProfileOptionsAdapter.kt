package com.example.to_dolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfileOptionsAdapter(private val options: List<String>) : RecyclerView.Adapter<ProfileOptionsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val optionText: TextView = itemView.findViewById(R.id.option_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileOptionsAdapter.ViewHolder, position: Int) {
        holder.optionText.text = options[position]
    }

    override fun getItemCount() = options.size

}
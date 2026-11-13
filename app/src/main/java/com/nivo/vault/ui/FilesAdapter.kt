package com.nivo.vault.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nivo.vault.R

class FilesAdapter(
    private val items: List<String>, 
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<FilesAdapter.VH>() {
    inner class VH(v: View) : RecyclerView.ViewHolder(v) { 
        val name: TextView = v.findViewById(R.id.fileName) 
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = 
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.name.text = items[position]
        holder.itemView.setOnClickListener { onClick(items[position]) }
    }
    override fun getItemCount() = items.size
}

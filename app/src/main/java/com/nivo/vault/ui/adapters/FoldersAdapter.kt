package com.nivo.vault.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nivo.vault.R
import java.io.File

class FoldersAdapter(private val items: List<File>, private val onClick: (File) -> Unit, private val onLongClick: (File) -> Unit) : RecyclerView.Adapter<FoldersAdapter.VH>() {
    inner class VH(v: View) : RecyclerView.ViewHolder(v) { val name: TextView = v.findViewById(R.id.folderName) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val folder = items[position]
        holder.name.text = "📁 ${folder.name}"
        holder.itemView.setOnClickListener { onClick(folder) }
        holder.itemView.setOnLongClickListener { onLongClick(folder); true }
    }
    override fun getItemCount() = items.size
}

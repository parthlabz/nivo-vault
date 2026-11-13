package com.nivo.vault.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nivo.vault.R
import com.nivo.vault.storage.VaultStorage
import java.io.File

class FilesAdapter(
    private val items: List<File>, 
    private val onClick: (File) -> Unit, 
    private val onLongClick: (File) -> Unit,
    private val onDelete: (File) -> Unit
) : RecyclerView.Adapter<FilesAdapter.VH>() {
    
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.fileName)
        val meta: TextView = v.findViewById(R.id.fileMeta)
        val delete: ImageButton = v.findViewById(R.id.deleteBtn)
        val icon: ImageView = v.findViewById(R.id.fileIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val file = items[position]
        holder.name.text = file.name
        holder.meta.text = VaultStorage.humanSize(file.length())
        
        val ext = file.extension.lowercase()
        val iconRes = when (ext) {
            "jpg", "jpeg", "png", "gif", "webp" -> android.R.drawable.ic_menu_gallery
            "mp4", "mkv", "avi", "mov" -> android.R.drawable.ic_media_play
            "mp3", "wav", "ogg", "m4a" -> android.R.drawable.ic_lock_silent_mode_off
            "pdf", "doc", "docx", "txt" -> android.R.drawable.ic_menu_agenda
            "zip", "rar", "7z" -> android.R.drawable.ic_menu_save
            else -> android.R.drawable.ic_menu_info_details
        }
        holder.icon.setImageResource(iconRes)

        holder.itemView.setOnClickListener { onClick(file) }
        holder.itemView.setOnLongClickListener { onLongClick(file); true }
        holder.delete.setOnClickListener { onDelete(file) }
    }

    override fun getItemCount() = items.size
}

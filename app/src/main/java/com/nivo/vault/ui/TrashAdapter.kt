package com.nivo.vault.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nivo.vault.R
import com.nivo.vault.model.FileUi
import com.nivo.vault.storage.VaultStorage
import java.text.SimpleDateFormat
import java.util.*

class TrashAdapter(
    private val onRestore: (FileUi) -> Unit,
    private val onDelete: (FileUi) -> Unit
) : RecyclerView.Adapter<TrashAdapter.VH>() {

    private val items = mutableListOf<FileUi>()
    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun submit(list: List<FileUi>) {
        items.clear(); items.addAll(list); notifyDataSetChanged()
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.trashName)
        val meta: TextView = v.findViewById(R.id.trashMeta)
        val restore: ImageButton = v.findViewById(R.id.restoreBtn)
        val del: ImageButton = v.findViewById(R.id.permDeleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_trash, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        holder.name.text = t.name
        val dateStr = if (t.deletedAt > 0) fmt.format(Date(t.deletedAt)) else "Unknown"
        holder.meta.text = "${VaultStorage.humanSize(t.sizeBytes)} • deleted $dateStr"
        holder.restore.setOnClickListener { onRestore(t) }
        holder.del.setOnClickListener { onDelete(t) }
    }

    override fun getItemCount() = items.size
}

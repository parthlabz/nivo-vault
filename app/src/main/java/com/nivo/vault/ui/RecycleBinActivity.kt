package com.nivo.vault.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nivo.vault.R
import com.nivo.vault.storage.VaultStorage
import com.nivo.vault.ui.adapters.FilesAdapter
import com.nivo.vault.util.ActivityLogger
import java.io.File

class RecycleBinActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_bin)
        
        rv = findViewById<RecyclerView>(R.id.trashRv)
        rv.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.emptyBinBtn).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Empty Bin?")
                .setMessage("This will permanently delete all files in the bin.")
                .setPositiveButton("Empty") { _, _ ->
                    VaultStorage.binDir(this).deleteRecursively()
                    VaultStorage.binDir(this).mkdirs()
                    ActivityLogger.log(this, "Bin Emptied", "All files permanently deleted")
                    loadBin()
                }
                .setNegativeButton("Cancel", null).show()
        }
        loadBin()
    }

    override fun onResume() {
        super.onResume()
        loadBin()
    }

    private fun loadBin() {
        val binDir = VaultStorage.binDir(this)
        val files = binDir.listFiles()?.filter { it.isFile }?.sortedByDescending { it.lastModified() } ?: emptyList()
        findViewById<TextView>(R.id.binSizeTv).text = "${VaultStorage.humanSize(VaultStorage.dirSizeBytes(binDir))} in Bin"
        
        rv.adapter = FilesAdapter(files,
            onClick = { file -> restoreFile(file) },
            onLongClick = { file -> confirmPermanentDelete(file) },
            onDelete = { file -> confirmPermanentDelete(file) }
        )
    }

    private fun restoreFile(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Restore File?")
            .setMessage("Restore ${file.name}? It will go back to a folder.")
            .setPositiveButton("Restore") { _, _ ->
                val targetDir = VaultStorage.foldersDir(this, "personal").listFiles()?.firstOrNull()
                if (targetDir != null) {
                    val dest = File(targetDir, file.name)
                    file.renameTo(dest)
                    ActivityLogger.log(this, "File Restored", file.name)
                    Toast.makeText(this, "Restored to ${targetDir.name}", Toast.LENGTH_SHORT).show()
                    loadBin()
                } else {
                    Toast.makeText(this, "No folder to restore to. Create a folder first.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun confirmPermanentDelete(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete Permanently?")
            .setMessage("This cannot be undone.")
            .setPositiveButton("Delete Forever") { _, _ ->
                file.delete()
                ActivityLogger.log(this, "File Shredded", file.name)
                Toast.makeText(this, "Deleted Permanently", Toast.LENGTH_SHORT).show()
                loadBin()
            }
            .setNegativeButton("Cancel", null).show()
    }
}

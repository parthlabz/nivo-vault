package com.nivo.vault.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nivo.vault.R
import com.nivo.vault.storage.VaultStorage
import com.nivo.vault.ui.adapters.FoldersAdapter
import com.nivo.vault.ui.adapters.SecurityManager
import java.io.File

class VaultActivity : AppCompatActivity() {
    private lateinit var vault: String
    private lateinit var rv: RecyclerView
    private lateinit var emptyTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)
        
        vault = intent.getStringExtra("vault") ?: "personal"
        findViewById<TextView>(R.id.vaultTitleTv).text = if (vault == "personal") "Personal Vault" else "Professional Vault"
        findViewById<Button>(R.id.recycleBinBtn).setOnClickListener { startActivity(Intent(this, RecycleBinActivity::class.java)) }
        
        rv = findViewById<RecyclerView>(R.id.foldersRv)
        emptyTv = findViewById(R.id.emptyView)
        rv.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.newFolderBtn).setOnClickListener {
            showCreateFolderDialog()
        }
        
        loadFolders()
    }

    override fun onResume() {
        super.onResume()
        loadFolders()
    }

    private fun showCreateFolderDialog() {
        val input = EditText(this).apply { hint = "Enter folder name..." }
        AlertDialog.Builder(this).setTitle("New Folder").setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val newFolder = File(VaultStorage.foldersDir(this, vault), name)
                    if (!newFolder.exists()) {
                        newFolder.mkdirs()
                        loadFolders()
                    } else Toast.makeText(this, "Already exists", Toast.LENGTH_SHORT).show()
                }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun loadFolders() {
        val dir = VaultStorage.foldersDir(this, vault)
        val folders = dir.listFiles()?.filter { it.isDirectory }?.sortedBy { it.name } ?: emptyList()
        findViewById<TextView>(R.id.vaultUsedTv).text = "${VaultStorage.humanSize(VaultStorage.dirSizeBytes(dir))} used"
        
        if (folders.isEmpty()) {
            emptyTv.visibility = TextView.VISIBLE
            rv.visibility = RecyclerView.GONE
        } else {
            emptyTv.visibility = TextView.GONE
            rv.visibility = RecyclerView.VISIBLE
        }
        
        rv.adapter = FoldersAdapter(folders, 
            onClick = { folder ->
                val i = Intent(this, FolderActivity::class.java)
                i.putExtra("vault", vault)
                i.putExtra("folderName", folder.name)
                startActivity(i)
            },
            onLongClick = { folder -> showFolderMenu(folder) }
        )
    }

    private fun showFolderMenu(folder: File) {
        val options = arrayOf("Rename", "Delete")
        AlertDialog.Builder(this).setTitle(folder.name).setItems(options) { _, which ->
            if (which == 0) renameFolder(folder)
            else confirmDeleteFolder(folder)
        }.show()
    }

    private fun renameFolder(folder: File) {
        val input = EditText(this).apply { setText(folder.name) }
        AlertDialog.Builder(this).setTitle("Rename Folder").setView(input)
            .setPositiveButton("Rename") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val newFolder = File(folder.parentFile, newName)
                    if (!newFolder.exists()) {
                        folder.renameTo(newFolder)
                        loadFolders()
                    } else Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show()
                }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun confirmDeleteFolder(folder: File) {
        val input = EditText(this).apply { 
            hint = "Enter PIN"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        AlertDialog.Builder(this).setTitle("Delete Folder?").setView(input)
            .setPositiveButton("Delete") { _, _ ->
                if (SecurityManager.checkPin(this, input.text.toString())) {
                    folder.listFiles()?.forEach { file ->
                        val dest = File(VaultStorage.binDir(this), file.name)
                        file.renameTo(dest)
                    }
                    folder.delete()
                    loadFolders()
                    Toast.makeText(this, "Folder Deleted", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
            }.setNegativeButton("Cancel", null).show()
    }
}

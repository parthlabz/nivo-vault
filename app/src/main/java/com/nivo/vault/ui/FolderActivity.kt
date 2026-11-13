package com.nivo.vault.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nivo.vault.R
import com.nivo.vault.storage.VaultStorage
import com.nivo.vault.ui.adapters.FilesAdapter
import com.nivo.vault.ui.adapters.SecurityManager
import com.nivo.vault.util.ActivityLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FolderActivity : AppCompatActivity() {
    private lateinit var vault: String
    private lateinit var folderName: String
    private lateinit var rv: RecyclerView
    private lateinit var dir: File
    private lateinit var emptyTv: TextView
    private lateinit var loadingOverlay: LinearLayout
    private lateinit var loadingText: TextView
    private lateinit var currentPhotoFile: File
    private var sortMode = 0

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            askForFileName("jpg") { newName ->
                showLoading("Saving photo...")
                lifecycleScope.launch(Dispatchers.IO) {
                    val finalFile = File(dir, "$newName.jpg")
                    currentPhotoFile.renameTo(finalFile)
                    ActivityLogger.log(this@FolderActivity, "Camera Capture", newName)
                    withContext(Dispatchers.Main) { hideLoading(); loadFiles() }
                }
            }
        } else if (currentPhotoFile.exists()) currentPhotoFile.delete()
    }

    private val pickFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val mimeType = contentResolver.getType(uri)
                val ext = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "file"
                askForFileName(ext) { newName ->
                    showLoading("Importing file...\nPlease wait")
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val destFile = File(dir, "$newName.$ext")
                            contentResolver.openInputStream(uri)?.use { input ->
                                destFile.outputStream().use { output -> input.copyTo(output) }
                            }
                            ActivityLogger.log(this@FolderActivity, "File Import", newName)
                            withContext(Dispatchers.Main) { hideLoading(); loadFiles() }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                hideLoading()
                                Toast.makeText(this@FolderActivity, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        
        vault = intent.getStringExtra("vault") ?: "personal"
        folderName = intent.getStringExtra("folderName") ?: "Folder"
        dir = File(VaultStorage.foldersDir(this, vault), folderName)
        findViewById<TextView>(R.id.folderTitleTv).text = folderName
        emptyTv = findViewById(R.id.emptyFilesView)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        loadingText = findViewById(R.id.loadingText)

        rv = findViewById<RecyclerView>(R.id.filesRv)
        rv.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.addFileBtn).setOnClickListener {
            AlertDialog.Builder(this).setTitle("Add File").setItems(arrayOf("Take Secure Photo", "Import Any File")) { _, which ->
                if (which == 0) launchCamera() else launchFilePicker()
            }.show()
        }

        findViewById<Button>(R.id.sortBtn).setOnClickListener {
            AlertDialog.Builder(this).setTitle("Sort By").setItems(arrayOf("Date", "Name", "Size")) { _, which ->
                sortMode = which
                loadFiles()
            }.show()
        }
        loadFiles()
    }

    private fun showLoading(text: String) {
        loadingText.text = text
        loadingOverlay.visibility = LinearLayout.VISIBLE
    }

    private fun hideLoading() {
        loadingOverlay.visibility = LinearLayout.GONE
    }

    private fun askForFileName(extension: String, onConfirm: (String) -> Unit) {
        val input = EditText(this).apply { hint = "Enter file name..." }
        AlertDialog.Builder(this).setTitle("Name your file").setView(input)
            .setPositiveButton("Save") { _, _ ->
                var name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    if (name.endsWith(".$extension", ignoreCase = true)) name = name.substring(0, name.length - extension.length - 1)
                    onConfirm(name)
                }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun launchCamera() {
        try {
            currentPhotoFile = File(dir, "temp_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", currentPhotoFile)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            takePicture.launch(intent)
        } catch (e: Exception) { Toast.makeText(this, "No camera app", Toast.LENGTH_SHORT).show() }
    }

    private fun launchFilePicker() {
        pickFile.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply { 
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" 
        })
    }

    private fun loadFiles() {
        var files = dir.listFiles()?.filter { it.isFile } ?: emptyList()
        
        files = when (sortMode) {
            0 -> files.sortedByDescending { it.lastModified() }
            1 -> files.sortedBy { it.name.lowercase() }
            2 -> files.sortedByDescending { it.length() }
            else -> files
        }

        findViewById<TextView>(R.id.folderUsedTv).text = "${VaultStorage.humanSize(VaultStorage.dirSizeBytes(dir))} used"
        
        if (files.isEmpty()) {
            emptyTv.visibility = TextView.VISIBLE
            rv.visibility = RecyclerView.GONE
        } else {
            emptyTv.visibility = TextView.GONE
            rv.visibility = RecyclerView.VISIBLE
        }
        
        rv.adapter = FilesAdapter(files, 
            onClick = { file -> openFile(file) },
            onLongClick = { file -> showFileDetails(file) },
            onDelete = { file -> moveToBin(file) }
        )
    }

    private fun openFile(file: File) {
        val ext = file.extension.lowercase()
        if (ext in listOf("jpg", "jpeg", "png", "gif", "webp")) {
            val i = Intent(this, MediaViewerActivity::class.java)
            i.putExtra("filePath", file.absolutePath)
            startActivity(i)
        } else {
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, contentResolver.getType(uri) ?: "*/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try { startActivity(Intent.createChooser(intent, "Open with")) } 
            catch (e: Exception) { Toast.makeText(this, "No app found", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun showFileDetails(file: File) {
        val sdf = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
        val details = "Name: ${file.name}\nSize: ${VaultStorage.humanSize(file.length())}\nModified: ${sdf.format(Date(file.lastModified()))}"
        AlertDialog.Builder(this).setTitle("File Details").setMessage(details).setPositiveButton("OK", null).show()
    }

    private fun moveToBin(file: File) {
        val input = EditText(this).apply { 
            hint = "Enter PIN"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        AlertDialog.Builder(this).setTitle("Security Check").setView(input)
            .setPositiveButton("Verify") { _, _ ->
                if (SecurityManager.checkPin(this, input.text.toString())) {
                    file.renameTo(File(VaultStorage.binDir(this), file.name))
                    ActivityLogger.log(this, "File Deleted", file.name)
                    loadFiles()
                } else Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
            }.setNegativeButton("Cancel", null).show()
    }
}

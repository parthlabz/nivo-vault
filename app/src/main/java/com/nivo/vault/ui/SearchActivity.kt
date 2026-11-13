package com.nivo.vault.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nivo.vault.R
import com.nivo.vault.storage.VaultStorage
import com.nivo.vault.ui.adapters.FilesAdapter
import java.io.File

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val rv = findViewById<RecyclerView>(R.id.searchRv)
        rv.layoutManager = LinearLayoutManager(this)
        val input = findViewById<EditText>(R.id.searchInput)

        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                performSearch(s?.toString() ?: "", rv)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun performSearch(query: String, rv: RecyclerView) {
        if (query.isEmpty()) { rv.adapter = null; return }
        
        val results = mutableListOf<File>()
        listOf("personal", "professional").forEach { vault ->
            VaultStorage.foldersDir(this, vault).walkTopDown().forEach { file ->
                if (file.isFile && file.name.contains(query, ignoreCase = true)) results.add(file)
            }
        }
        
        rv.adapter = FilesAdapter(results, 
            onClick = { },
            onLongClick = { },
            onDelete = { }
        )
    }
}

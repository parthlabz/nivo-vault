package com.nivo.vault.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.nivo.vault.R
import java.io.File

class MediaViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_viewer)
        intent.getStringExtra("filePath")?.let { path ->
            val file = File(path)
            if (file.exists()) {
                findViewById<ImageView>(R.id.fullImageView).setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            }
        }
    }
}

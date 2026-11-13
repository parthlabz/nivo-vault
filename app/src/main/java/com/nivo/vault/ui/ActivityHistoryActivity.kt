package com.nivo.vault.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nivo.vault.R
import com.nivo.vault.util.ActivityLogger

class ActivityHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        findViewById<TextView>(R.id.historyText).text = ActivityLogger.getLogs(this)
    }
}

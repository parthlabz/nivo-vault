package com.nivo.vault.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nivo.vault.R

class WarningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_warning)
        findViewById<Button>(R.id.ackBtn).setOnClickListener {
            getSharedPreferences("nivo_settings", MODE_PRIVATE).edit().putBoolean("warning_shown", true).apply()
            startActivity(Intent(this, LockActivity::class.java))
            finish()
        }
    }
}

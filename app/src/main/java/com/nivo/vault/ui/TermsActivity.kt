package com.nivo.vault.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nivo.vault.R

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        findViewById<Button>(R.id.agreeBtn).setOnClickListener {
            getSharedPreferences("nivo_settings", MODE_PRIVATE).edit().putBoolean("terms_accepted", true).apply()
            startActivity(Intent(this, WarningActivity::class.java))
            finish()
        }
    }
}

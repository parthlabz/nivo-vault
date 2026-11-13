package com.nivo.vault.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nivo.vault.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        
        val prefs = getSharedPreferences("nivo_settings", MODE_PRIVATE)
        
        // First-time onboarding flow:
        // 1. Terms of Use
        // 2. Data loss warning
        // 3. Lock screen (every launch)
        if (!prefs.getBoolean("terms_accepted", false)) {
            startActivity(Intent(this, TermsActivity::class.java))
            finish()
            return
        }
        if (!prefs.getBoolean("warning_shown", false)) {
            startActivity(Intent(this, WarningActivity::class.java))
            finish()
            return
        }
        
        findViewById<Button>(R.id.getStartedBtn).setOnClickListener {
            startActivity(Intent(this, LockActivity::class.java))
            finish()
        }
    }
}

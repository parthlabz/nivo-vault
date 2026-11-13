package com.nivo.vault.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nivo.vault.R
import com.nivo.vault.security.SecurityPrefs

class SecuritySetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_setup)
        val prefs = SecurityPrefs(this)

        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            prefs.setAppPassword("1234")
            prefs.appLockEnabled = true
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

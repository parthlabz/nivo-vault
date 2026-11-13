package com.nivo.vault.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.nivo.vault.R
import com.nivo.vault.ui.adapters.SecurityManager

class LockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        
        val input = findViewById<EditText>(R.id.pinInput)
        
        findViewById<Button>(R.id.unlockBtn).setOnClickListener {
            if (SecurityManager.checkPin(this, input.text.toString())) {
                goToMain()
            } else {
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.fingerprintBtn).setOnClickListener {
            showBiometricPrompt()
        }

        showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
            val executor = ContextCompat.getMainExecutor(this)
            val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    goToMain()
                }
            })
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock Nivo Vault")
                .setSubtitle("Use your fingerprint")
                .setNegativeButtonText("Use PIN")
                .build()
            prompt.authenticate(promptInfo)
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

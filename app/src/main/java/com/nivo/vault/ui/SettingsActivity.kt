package com.nivo.vault.ui

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nivo.vault.R
import com.nivo.vault.storage.VaultStorage
import com.nivo.vault.ui.adapters.SecurityManager
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private val UPI_ID = "patel-076@ptaxis"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<TextView>(R.id.settingsText).text = "Vault Path:\n/data/data/com.nivo.vault/files/vault"

        findViewById<Button>(R.id.setPinBtn).setOnClickListener {
            val input = EditText(this).apply { 
                hint = "Enter new 4-digit PIN"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
            AlertDialog.Builder(this).setTitle("Change PIN").setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val newPin = input.text.toString()
                    if (newPin.length == 4) {
                        SecurityManager.setPin(this, newPin)
                        Toast.makeText(this, "PIN Changed Successfully", Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show()
                }.setNegativeButton("Cancel", null).show()
        }

        findViewById<Button>(R.id.autoLockBtn).setOnClickListener {
            val options = arrayOf("Immediately", "30 seconds", "1 minute", "5 minutes")
            AlertDialog.Builder(this).setTitle("Auto-Lock Timer").setItems(options) { _, which ->
                val seconds = when (which) { 0 -> 0; 1 -> 30; 2 -> 60; 3 -> 300; else -> 30 }
                getSharedPreferences("nivo_settings", MODE_PRIVATE).edit().putInt("auto_lock_seconds", seconds).apply()
                Toast.makeText(this, "Auto-lock set to ${options[which]}", Toast.LENGTH_SHORT).show()
            }.show()
        }

        findViewById<Button>(R.id.exportBtn).setOnClickListener {
            try {
                val exportDir = File(android.os.Environment.getExternalStorageDirectory(), "NivoVault_Backup")
                exportDir.mkdirs()
                VaultStorage.root(this).copyRecursively(exportDir, true)
                Toast.makeText(this, "Exported to ${exportDir.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.githubBtn).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/parthlabz")))
        }
        findViewById<TextView>(R.id.linkedinBtn).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/parth-patel-2750a5423")))
        }

        findViewById<Button>(R.id.payUpiBtn).setOnClickListener { openUpiPayment() }

        findViewById<Button>(R.id.copyUpiBtn).setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", UPI_ID))
            Toast.makeText(this, "UPI ID copied ❤️", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUpiPayment() {
        val upiUri = Uri.parse("upi://pay?pa=$UPI_ID&pn=Parth%20Patel&cu=INR&tn=Donation%20for%20Nivo%20Vault")
        val intent = Intent(Intent.ACTION_VIEW, upiUri)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            AlertDialog.Builder(this)
                .setTitle("No UPI App Found")
                .setMessage("Please install Google Pay, PhonePe, or Paytm to donate.\n\nOr copy the UPI ID:\n$UPI_ID")
                .setPositiveButton("Copy UPI ID") { _, _ ->
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", UPI_ID))
                    Toast.makeText(this, "UPI ID copied ❤️", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("OK", null)
                .show()
        }
    }
}

package com.nivo.vault.ui

import android.content.Intent
import android.os.Bundle
import android.os.StatFs
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nivo.vault.R
import com.nivo.vault.storage.VaultStorage
import com.nivo.vault.ui.adapters.SecurityManager
import com.nivo.vault.ui.custom.CircularStorageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        SecurityManager.setPin(this, "1234")

        findViewById<LinearLayout>(R.id.personalBtn).setOnClickListener { openVault("personal") }
        findViewById<LinearLayout>(R.id.professionalBtn).setOnClickListener { openVault("professional") }
        findViewById<LinearLayout>(R.id.navProjects).setOnClickListener { openVault("personal") }
        findViewById<LinearLayout>(R.id.navSettings).setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        findViewById<FrameLayout>(R.id.fabAdd).setOnClickListener { openVault("personal") }

        findViewById<LinearLayout>(R.id.navSearch).setOnClickListener { 
            val popup = android.widget.PopupMenu(this, findViewById(R.id.navSearch))
            popup.menu.add("Search Files").setOnMenuItemClickListener {
                startActivity(Intent(this, SearchActivity::class.java)); true
            }
            popup.menu.add("Activity History").setOnMenuItemClickListener {
                startActivity(Intent(this, ActivityHistoryActivity::class.java)); true
            }
            popup.show()
        }

        val p = VaultStorage.dirSizeBytes(VaultStorage.vaultDir(this, "personal"))
        val w = VaultStorage.dirSizeBytes(VaultStorage.vaultDir(this, "professional"))
        val total = p + w
        
        findViewById<TextView>(R.id.personalSizeTv).text = VaultStorage.humanSize(p)
        findViewById<TextView>(R.id.professionalSizeTv).text = VaultStorage.humanSize(w)

        val stat = StatFs(applicationContext.filesDir.path)
        val totalDevice = stat.blockCountLong * stat.blockSizeLong
        val free = stat.availableBlocksLong * stat.blockSizeLong
        val percent = if (totalDevice > 0) ((total * 100) / totalDevice).toInt() else 0
        
        findViewById<TextView>(R.id.storagePercentTv).text = "$percent%"
        findViewById<CircularStorageView>(R.id.storageRing).setProgress(percent)
        findViewById<TextView>(R.id.totalTv).text = "Total: ${VaultStorage.humanSize(total)} • Free: ${VaultStorage.humanSize(free)}"

        val deviceUsagePercent = ((totalDevice - free) * 100) / totalDevice
        if (deviceUsagePercent > 90) {
            Toast.makeText(this, "⚠️ Device storage is ${deviceUsagePercent}% full. New file imports may fail.", Toast.LENGTH_LONG).show()
        }
    }

    // REMOVED: onPause auto-lock (no more redirecting to Home)

    private fun openVault(v: String) {
        val i = Intent(this, VaultActivity::class.java)
        i.putExtra("vault", v)
        startActivity(i)
    }
}

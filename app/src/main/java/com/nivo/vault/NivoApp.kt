package com.nivo.vault

import android.app.Application
import com.nivo.vault.storage.VaultStorage

class NivoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        VaultStorage.ensureBaseDirs(this)
    }
}

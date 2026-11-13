package com.nivo.vault.storage

import android.content.Context
import java.io.File

object VaultStorage {
    fun root(ctx: Context): File = File(ctx.filesDir, "vault").apply { mkdirs() }
    fun vaultDir(ctx: Context, vault: String): File = File(root(ctx), vault).apply { mkdirs() }
    fun foldersDir(ctx: Context, vault: String): File = File(vaultDir(ctx, vault), "folders").apply { mkdirs() }
    fun folderDir(ctx: Context, vault: String, folderName: String): File = File(foldersDir(ctx, vault), folderName).apply { mkdirs() }
    fun binDir(ctx: Context): File = File(root(ctx), "bin").apply { mkdirs() }

    fun ensureBaseDirs(ctx: Context) {
        vaultDir(ctx, "personal")
        vaultDir(ctx, "professional")
        binDir(ctx)
    }

    fun dirSizeBytes(dir: File): Long {
        if (!dir.exists()) return 0L
        var total = 0L
        dir.walkTopDown().forEach { if (it.isFile) total += it.length() }
        return total
    }

    fun humanSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val units = arrayOf("KB", "MB", "GB", "TB")
        var v = bytes.toDouble() / 1024.0
        var i = 0
        while (v >= 1024 && i < units.lastIndex) { v /= 1024; i++ }
        return String.format("%.2f %s", v, units[i])
    }
}

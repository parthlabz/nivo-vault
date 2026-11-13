package com.nivo.vault.util

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ActivityLogger {
    private const val LOG_FILE = "activity_log.txt"

    fun log(ctx: Context, action: String, detail: String) {
        try {
            val logFile = File(ctx.filesDir, LOG_FILE)
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val logLine = "[$timestamp] $action: $detail\n"
            logFile.appendText(logLine)
        } catch (e: Exception) { }
    }

    fun getLogs(ctx: Context): String {
        val logFile = File(ctx.filesDir, LOG_FILE)
        return if (logFile.exists()) logFile.readText() else "No activity yet."
    }
}

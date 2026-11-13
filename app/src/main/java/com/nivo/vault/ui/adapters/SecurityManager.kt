package com.nivo.vault.ui.adapters

import android.content.Context

object SecurityManager {
    private const val PREFS = "nivo_sec"
    private const val KEY_PIN = "deletion_pin"

    fun setPin(ctx: Context, pin: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_PIN, pin).apply()
    }
    
    fun checkPin(ctx: Context, pin: String): Boolean {
        val stored = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_PIN, "1234")
        return stored == pin
    }
}

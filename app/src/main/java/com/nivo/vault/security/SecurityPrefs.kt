package com.nivo.vault.security

import android.content.Context
import java.security.MessageDigest

class SecurityPrefs(ctx: Context) {
    private val prefs = ctx.getSharedPreferences("nivo_app_security", Context.MODE_PRIVATE)

    var appLockEnabled: Boolean
        get() = prefs.getBoolean("appLockEnabled", false)
        set(v) = prefs.edit().putBoolean("appLockEnabled", v).apply()

    fun setAppPassword(pw: String) { prefs.edit().putString("appPwHash", sha256(pw)).apply() }
    fun hasAppPassword(): Boolean = prefs.contains("appPwHash")
    fun checkAppPassword(pw: String): Boolean = prefs.getString("appPwHash", null) == sha256(pw)

    fun setPanicPassword(pw: String) { prefs.edit().putString("panicPwHash", sha256(pw)).apply() }
    fun hasPanicPassword(): Boolean = prefs.contains("panicPwHash")
    fun checkPanicPassword(pw: String): Boolean = prefs.getString("panicPwHash", null) == sha256(pw)

    var fingerprintEnabled: Boolean
        get() = prefs.getBoolean("fingerprintEnabled", false)
        set(v) = prefs.edit().putBoolean("fingerprintEnabled", v).apply()

    fun failedAttempts(): Int = prefs.getInt("failedAttempts", 0)
    fun recordFailure() {
        val n = failedAttempts() + 1
        prefs.edit().putInt("failedAttempts", n).apply()
    }
    fun clearFailures() { prefs.edit().putInt("failedAttempts", 0).apply() }

    private fun sha256(s: String): String =
        MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString("") { "%02x".format(it) }
}

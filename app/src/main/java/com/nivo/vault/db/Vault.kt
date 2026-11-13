package com.nivo.vault.db

object Vault {
    const val PERSONAL = "personal"
    const val PROFESSIONAL = "professional"
    fun displayName(id: String): String = if (id == PERSONAL) "Personal Vault" else "Professional Vault"
}

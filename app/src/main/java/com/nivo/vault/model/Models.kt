package com.nivo.vault.model

data class FolderUi(val id: Long, val vault: String, val name: String, val createdAt: Long)
data class ProjectUi(val id: Long, val vault: String, val name: String, val description: String, val createdAt: Long)
data class FileUi(
    val id: Long, 
    val folderId: Long, 
    val vault: String, 
    val name: String, 
    val sizeBytes: Long, 
    val importedAt: Long, 
    val deleted: Boolean = false,
    val deletedAt: Long = 0L,
    val originalFolderId: Long = 0L,
    val tags: String = "",
    val description: String = "",
    val important: Boolean = false,
    val versionTag: String = ""
)

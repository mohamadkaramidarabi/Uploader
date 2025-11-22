package ir.sharif.drive.uploader.cache.entity

import ir.sharif.drive.uploader.models.States

data class UploadItem(
    val id: Long,
    val name: String,
    val progress: Int,
    val size: Long,
    val state: States.UploadInfo.State
)
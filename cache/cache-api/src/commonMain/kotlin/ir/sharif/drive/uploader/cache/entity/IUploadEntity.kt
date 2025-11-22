package ir.sharif.drive.uploader.cache.entity

import ir.sharif.drive.uploader.models.States

interface IUploadEntity {
    val id: Long
    val serverId: String?
    val key: String?
    val fileName: String
    val filePath: String
    val fileSize: Long
    val chunkSize: Long?
    val chunkCount: Int?
    val state: States.UploadInfo.State
    val folderId: String?
    val versionGroup: String?
    val cloudPath: String
}

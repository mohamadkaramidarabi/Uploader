package ir.sharif.drive.uploader.cache.entity

import ir.sharif.drive.uploader.models.States


interface ILinkEntity {
    val id: Long
    val uploadId: Long
    val eTag: String?
    val state: States.Link.State
    val retryCount: Int
    val chunkSize: Long
    val url: String
}


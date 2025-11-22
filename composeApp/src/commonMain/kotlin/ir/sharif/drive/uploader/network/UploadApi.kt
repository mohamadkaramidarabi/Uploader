package ir.sharif.drive.uploader.network

import ir.sharif.drive.uploader.network.models.CompleteUploadRequest
import ir.sharif.drive.uploader.network.models.StartUploadResponse

interface UploadApi {
    suspend fun startUpload(size: Long): StartUploadResponse
    suspend fun putChunk(url: String, chunkData: ByteArray, contentLength: Long): String?
    suspend fun completeUpload(request: CompleteUploadRequest): Unit
}
